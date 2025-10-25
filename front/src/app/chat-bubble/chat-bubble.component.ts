import { Component, ElementRef, ViewChild, AfterViewChecked, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: string;
  attachments?: Attachment[];
}

interface Attachment {
  name: string;
  type: string;
  url?: string;
}

@Component({
  selector: 'app-chat-bubble',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './chat-bubble.component.html',
  styleUrls: ['./chat-bubble.component.css']
})
export class ChatBubbleComponent implements AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  @ViewChild('messageTextarea') private messageTextarea!: ElementRef;
  @ViewChild('fileInput') private fileInput!: ElementRef;

  isOpen = false;
  userInput = '';
  loading = false;
  isRecording = false;
  isDragging = false;
  selectedFile: File | null = null;
  
  messages: ChatMessage[] = [];

  constructor(private http: HttpClient) {}

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  // Keyboard shortcut to open chat (Cmd/Ctrl + K)
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if ((event.metaKey || event.ctrlKey) && event.key === 'k') {
      event.preventDefault();
      this.toggleChat();
    }
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      setTimeout(() => {
        this.focusTextarea();
      }, 300);
    }
  }

  scrollToBottom(): void {
    try {
      if (this.messagesContainer?.nativeElement) {
        this.messagesContainer.nativeElement.scrollTop = 
          this.messagesContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Scroll error:', err);
    }
  }

  focusTextarea(): void {
    if (this.messageTextarea?.nativeElement) {
      this.messageTextarea.nativeElement.focus();
    }
  }

  getCurrentTime(): string {
    return new Date().toLocaleTimeString('fr-FR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  }

  onTextareaEnter(event: Event): void {
  const keyboardEvent = event as KeyboardEvent;
  if (keyboardEvent.key === 'Enter' && !keyboardEvent.shiftKey) {
    event.preventDefault();
    this.sendMessage();
  }
}

  // === FILE ATTACHMENT FUNCTIONALITY ===
  triggerFileInput(): void {
    console.log('Triggering file input...');
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.click();
    } else {
      console.error('File input not found!');
    }
  }

  onFileSelected(event: Event): void {
    console.log('File selected event triggered');
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (file) {
      console.log('File selected:', file.name, file.type, file.size);
      this.selectedFile = file;
      
      // Auto-focus back to textarea after file selection
      setTimeout(() => {
        this.focusTextarea();
      }, 100);
    } else {
      console.log('No file selected');
    }
  }

  removeAttachment(): void {
    console.log('Removing attachment');
    this.selectedFile = null;
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
  }

  // Drag and drop functionality
  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
  }

  @HostListener('drop', ['$event'])
  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      const file = files[0];
      console.log('File dropped:', file.name, file.type, file.size);
      this.selectedFile = file;
    }
  }

  // === VOICE RECORDING FUNCTIONALITY ===
  async toggleRecording(): Promise<void> {
    if (this.isRecording) {
      this.stopRecording();
    } else {
      await this.startRecording();
    }
  }

  async startRecording(): Promise<void> {
    try {
      if (!navigator.mediaDevices?.getUserMedia) {
        alert('Votre navigateur ne supporte pas l\'enregistrement audio.');
        return;
      }

      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.isRecording = true;
      
      setTimeout(() => {
        if (this.isRecording) {
          this.stopRecording();
          this.userInput = "Ceci est une transcription simulée de votre message vocal. [Audio]";
          stream.getTracks().forEach(track => track.stop());
        }
      }, 3000);
      
    } catch (error) {
      console.error('Error accessing microphone:', error);
      alert('Impossible d\'accéder au microphone. Vérifiez les permissions.');
    }
  }

  stopRecording(): void {
    this.isRecording = false;
  }

  // === ENHANCED MESSAGE SENDING ===
  sendMessage(): void {
    const content = this.userInput.trim();
    if (!content && !this.selectedFile) {
      console.log('No content or file to send');
      return;
    }

    // Store the file reference BEFORE clearing it
    const fileToSend = this.selectedFile;
    console.log('Sending message with file:', fileToSend?.name);

    const attachments: Attachment[] = fileToSend ? [{
      name: fileToSend.name,
      type: fileToSend.type,
      url: URL.createObjectURL(fileToSend)
    }] : [];

    // Add user message with attachments
    this.messages.push({ 
      role: 'user', 
      content: content || `Fichier: ${fileToSend?.name}`,
      timestamp: this.getCurrentTime(),
      attachments
    });
    
    this.userInput = '';
    this.selectedFile = null;
    if (this.fileInput?.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
    this.loading = true;

    this.autoResizeTextarea();

    // Prepare request data
    const requestData: any = {
      messages: this.messages.map(msg => ({
        role: msg.role,
        content: msg.content
      }))
    };

    // Add file info if present
    if (fileToSend) {
      requestData.fileName = fileToSend.name;
      requestData.fileType = fileToSend.type;
      // If you want to send the actual file content, you'd need to convert to base64
      // this.fileToBase64(fileToSend).then(base64 => {
      //   requestData.fileContent = base64;
      // });
    }

    console.log('Sending request:', requestData);

    this.http.post<any>('http://localhost:8081/api/ai/chat', requestData).subscribe({
      next: (res) => {
        this.loading = false;
        this.messages.push({ 
          role: 'assistant', 
          content: res.content || 'Je ne peux pas répondre pour le moment.',
          timestamp: this.getCurrentTime()
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('API Error:', error);
        this.messages.push({ 
          role: 'assistant', 
          content: '⚠️ Erreur de connexion au service AI. Veuillez réessayer.',
          timestamp: this.getCurrentTime()
        });
      }
    });
  }

  private autoResizeTextarea(): void {
    if (this.messageTextarea?.nativeElement) {
      const textarea = this.messageTextarea.nativeElement;
      textarea.style.height = 'auto';
      textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px';
    }
  }

  // === QUICK ACTIONS ===
  onSuggestionClick(suggestion: string): void {
    this.userInput = suggestion;
    this.focusTextarea();
  }

  quickAction(action: string): void {
    switch (action) {
      case 'clear':
        const welcomeMessage = this.messages.find(msg => 
          msg.role === 'assistant' && msg.content.includes('Bonjour')
        );
        this.messages = welcomeMessage ? [welcomeMessage] : [];
        break;
      case 'export':
        this.exportChat();
        break;
      case 'help':
        this.userInput = 'Quelles sont tes fonctionnalités?';
        this.sendMessage();
        break;
    }
  }

  private exportChat(): void {
    const chatContent = this.messages.map(msg => 
      `${msg.role === 'user' ? 'Utilisateur' : 'Evalia AI'} (${msg.timestamp}): ${msg.content}`
    ).join('\n\n');
    
    const blob = new Blob([chatContent], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `chat-evalia-${new Date().toISOString().split('T')[0]}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // === FILE UTILITIES ===
  downloadAttachment(attachment: Attachment): void {
    if (attachment.url) {
      const a = document.createElement('a');
      a.href = attachment.url;
      a.download = attachment.name;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
    }
  }

  getFileSize(bytes: number | undefined): string {
    if (!bytes || bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  // Optional: Convert file to base64 for sending actual file content
  private async fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = error => reject(error);
    });
  }
}