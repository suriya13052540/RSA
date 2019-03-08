import { Component, OnInit } from '@angular/core';
import {UploadService} from '../service/upload.service';
import { HttpResponse, HttpEventType,HttpClient } from '@angular/common/http';
import {MatSnackBar} from '@angular/material';
import { saveAs } from 'file-saver';

import { Injectable } from '@angular/core';


 
@Injectable({
  providedIn: 'root'
})

@Component({
  selector: 'app-encry',
  templateUrl: './encry.component.html',
  styleUrls: ['./encry.component.css']
})
export class EncryComponent implements OnInit {

  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };
  constructor(private uploadService: UploadService,private http:HttpClient,private snackBar: MatSnackBar) { }
  ngOnInit() {
  }
 
  selectFile(event) {
    this.selectedFiles = event.target.files;
  }
 
  upload() {
    this.progress.percentage = 0;
 
    this.currentFileUpload = this.selectedFiles.item(0);
    this.uploadService.pushFileToStorage(this.currentFileUpload).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round(100 * event.loaded / event.total);
      } else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
        this.snackBar.open("เข้ารหัสเรียบร้อย", "OK", {
          duration: 10000,
          verticalPosition:"top",
          horizontalPosition: "center"});
      }
    },
    error =>{
    this.snackBar.open("ผิดพลาด", "ลองใหม่", {
      duration: 10000,
      verticalPosition:"top",
      horizontalPosition: "center"
    });});
 this.selectedFiles = undefined;
  }




  downloadEncrypFile() {
    this.http.get('http://localhost:8080/downloadFile/Encryp', {responseType: "blob", headers: {'Accept': 'text/plain'}})
  .subscribe(blob => {
    saveAs(blob,'EncryptionFile.txt');
  },error =>{this.snackBar.open("ผิดพลาด หรือ กุญแจถูกดาวน์โหลดแล้ว", "ลองใหม่", {
    duration: 10000,
    verticalPosition:"top",
    horizontalPosition: "center"
  })});
}


downloadkeyFile() {
  this.http.get('http://localhost:8080/downloadFile/key', {responseType: "blob"})
.subscribe(blob => {
  saveAs(blob,'DecryptionFile.key');
},error =>{this.snackBar.open("ผิดพลาด หรือ กุญแจถูกดาวน์โหลดแล้ว", "ลองใหม่", {
  duration: 10000,
  verticalPosition:"top",
  horizontalPosition: "center"
})});
}

}
