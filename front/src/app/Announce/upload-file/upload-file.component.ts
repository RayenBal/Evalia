import { Component, OnInit } from '@angular/core';
import { announce } from '../AnnounceModel/announce';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpEventType, HttpResponse } from '@angular/common/http';
@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [],
  templateUrl: './upload-file.component.html',
  styleUrl: './upload-file.component.css'
})
export class UploadFileComponent implements OnInit {

announces:announce[] = [];
  private idAnnouncement :string ='announ';
  selectedFile : File | null =null;

constructor(private announceService: AnnounceServiceService, private route: ActivatedRoute, private router: Router) { }



  ngOnInit(): void {
    // Retrieve the faculteCode from the route parameters
    this.route.params.subscribe(params => {
      this.idAnnouncement = params['id'];
      console.log('Announce ID:', this.idAnnouncement);
    });
  }

onFileSelected(event: any): void {
    const fileInput = event.target as HTMLInputElement;

    if (fileInput.files && fileInput.files.length > 0) {
      this.selectedFile = fileInput.files[0];
    } else {
      this.selectedFile = null;
    }
  }

  onUpload(): void {
    if (this.selectedFile) {
      // Use the AnnounceService to upload the file
      console.log(this.idAnnouncement);
  
      this.announceService.uploadPhoto(this.idAnnouncement, this.selectedFile).subscribe(
        (event: any) => {
          if (event.type === HttpEventType.UploadProgress) {
            // const percentDone = Math.round((100 * event.loaded) / event.total);
            // console.log(`File is ${percentDone}% uploaded.`);
          } else if (event instanceof HttpResponse) {
            console.log('File is completely uploaded!', event);
            this.router.navigate(['/getAllAnnounces']);
          }
        },
        (error: any) => {
          console.error('Error uploading file:', error);          
        }
      );
      
    }
  }

}

