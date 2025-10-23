import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-transfers',
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './transfers.html',
  styleUrl: './transfers.css'
})
export class Transfers {}
