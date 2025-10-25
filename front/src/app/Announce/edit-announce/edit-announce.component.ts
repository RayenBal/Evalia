import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { FormArray } from '@angular/forms';
@Component({
  selector: 'app-edit-announce',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-announce.component.html',
  styleUrl: './edit-announce.component.css'
})
export class EditAnnounceComponent implements OnInit{
 baseUrl = 'http://localhost:8081/Announcement';
  announceId: string = '';
  announceForm!: FormGroup;
  selectedMainImage?: File;
  selectedProductImages: File[] = [];

  existingMainImageUrl: string = '';
  existingProductImages: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private announceService: AnnounceServiceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
    this.announceService.getAnnounce(this.announceId).subscribe((data: announce) => {
      this.announceForm = this.fb.group({
        announceName: [data.announceName, Validators.required],
        content: [data.content, Validators.required],
        officeAddress: [data.officeAddress],
        deliveryAddress: [data.deliveryAddress],
        testModes: [data.testModes],

       quizList: this.fb.array(
  (data.quizList ?? []).map(quiz => this.fb.group({
    content: [quiz.content],
    questions: this.fb.array((quiz.questions ?? []).map(q => this.fb.group({
      content: [q.content],
      responses: this.fb.array((q.responses ?? []).map(r => this.fb.group({
        content: [r.content]
      })))
    })))
  }))
)

      });

      this.existingMainImageUrl = this.getImageUrl(data.image || '');
      this.existingProductImages = data.productImages ? data.productImages.split(',') : [];
    });
  }

  /*getImageUrl(fileName: string): string {
    return `http://localhost:8081/Announcement/downloadannounce/${fileName}`;
  }*/
  getImageUrl(fileName: string | undefined): string {
  return fileName ? `http://localhost:8081/Announcement/downloadannounce/${fileName}` : '';
}

  onMainImageSelected(event: any): void {
    this.selectedMainImage = event.target.files[0];
  }

  onProductImagesSelected(event: any): void {
    this.selectedProductImages = Array.from(event.target.files);
  }

  onSubmit(): void {
    if (this.announceForm.valid) {
      const formData = new FormData();
      formData.append('announceData', JSON.stringify(this.announceForm.value));

      if (this.selectedMainImage) {
        formData.append('image', this.selectedMainImage);
      }

      for (let file of this.selectedProductImages) {
        formData.append('productImages', file);
      }

      this.announceService.updateAnnounceWithImages(this.announceId, formData).subscribe(() => {
        alert('Annonce mise à jour avec succès !');
        this.router.navigate(['/announcements']);
      });
    }
  }
// Accès aux FormArray
get quizList(): FormArray {
  return this.announceForm.get('quizList') as FormArray;
}

getQuestions(quizIndex: number): FormArray {
  return this.quizList.at(quizIndex).get('questions') as FormArray;
}

getResponses(quizIndex: number, questionIndex: number): FormArray {
  return this.getQuestions(quizIndex).at(questionIndex).get('responses') as FormArray;
}
addQuiz(): void {
  this.quizList.push(this.fb.group({
    content: [''],
    questions: this.fb.array([])
  }));
}

removeQuiz(index: number): void {
  this.quizList.removeAt(index);
}

addQuestion(quizIndex: number): void {
  this.getQuestions(quizIndex).push(this.fb.group({
    content: [''],
    responses: this.fb.array([])
  }));
}

removeQuestion(quizIndex: number, questionIndex: number): void {
  this.getQuestions(quizIndex).removeAt(questionIndex);
}

addResponse(quizIndex: number, questionIndex: number): void {
  this.getResponses(quizIndex, questionIndex).push(this.fb.group({
    content: ['']
  }));
}

removeResponse(quizIndex: number, questionIndex: number, responseIndex: number): void {
  this.getResponses(quizIndex, questionIndex).removeAt(responseIndex);
}

}
