import { Component, OnInit } from '@angular/core';
import {UploadService} from '../service/upload.service';
import { HttpResponse, HttpEventType,HttpClient } from '@angular/common/http';
import {MatSnackBar} from '@angular/material';
import { saveAs } from 'file-saver';

import { Injectable } from '@angular/core';

@Component({
  selector: 'app-decry',
  templateUrl: './decry.component.html',
  styleUrls: ['./decry.component.css']
})
export class DecryComponent implements OnInit {

  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };


  selectedFileskey: FileList;
  currentFileUploadkey: File;
  progresskey: { percentage: number } = { percentage: 0 };

  constructor(private uploadService: UploadService,private http:HttpClient,private snackBar: MatSnackBar) { }
  ngOnInit() {
  }
  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  selectFilekey(event) {
    this.selectedFileskey = event.target.files;
  }

  uploaden() {
    this.progress.percentage = 0;
 
    this.currentFileUpload = this.selectedFiles.item(0);
    this.uploadService.pushFileToStorageEncryp(this.currentFileUpload).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progress.percentage = Math.round(100 * event.loaded / event.total);
      } else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
        this.snackBar.open("บันทึกไฟล์เรียบร้อย", "OK", {
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



  uploadkey() {
    this.progresskey.percentage = 0;
 
    this.currentFileUploadkey = this.selectedFileskey.item(0);
    this.uploadService.pushFileToStoragekey(this.currentFileUploadkey).subscribe(event => {
      if (event.type === HttpEventType.UploadProgress) {
        this.progresskey.percentage = Math.round(100 * event.loaded / event.total);
      } else if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
        this.snackBar.open("บันทึกไฟล์เรียบร้อย", "OK", {
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
 this.selectedFileskey = undefined;
  }


  downloadDecrypFile() {
    this.http.get('http://localhost:8080/downloadFile/loadDecryption', {responseType: "blob", headers: {'Accept': 'text/plain'}})
  .subscribe(blob => {
    saveAs(blob,'DecryptionFile.txt');
  },error =>{this.snackBar.open("ผิดพลาด หรือ ไฟล์ถูกดาวน์โหลดแล้ว", "ลองใหม่", {
    duration: 10000,
    verticalPosition:"top",
    horizontalPosition: "center"
  })});


}

}
