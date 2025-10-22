import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-extract',
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './extract.html',
  styleUrl: './extract.css'
})
export class Extract {}
