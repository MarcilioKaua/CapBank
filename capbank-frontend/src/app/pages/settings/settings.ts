import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-settings',
  imports: [CommonModule, MatCardModule, MatIconModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css'
})
export class Settings {}
