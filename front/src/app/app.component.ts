import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ChatBubbleComponent } from './chat-bubble/chat-bubble.component'; // 👈 import chat component

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, NgbModule, ChatBubbleComponent], // 👈 include here
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'] // 👈 fixed plural property name
})
export class AppComponent {
  title = 'EvaliaFront';
}
