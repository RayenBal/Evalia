import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';  // ✅ Import FormsModule
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface ChatMessage {
  role: string;
  content: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule], // ✅ Added FormsModule
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent {
  messages: ChatMessage[] = [];
  userMessage = '';
  apiUrl = 'http://localhost:8081/api/ai/chat';

  constructor(private http: HttpClient) {}

  sendMessage() {
    const text = this.userMessage.trim();
    if (!text) return;

    this.messages.push({ role: 'user', content: text });
    this.userMessage = '';

    this.http.post<any>(this.apiUrl, {
      messages: [{ role: 'user', content: text }]
    }).subscribe({
      next: (res) => {
        this.messages.push({
          role: 'assistant',
          content: res.content || '(no response)'
        });
      },
      error: (err) => {
        console.error('Chat error:', err);
        this.messages.push({
          role: 'assistant',
          content: '⚠️ Error connecting to backend.'
        });
      }
    });
  }
}
