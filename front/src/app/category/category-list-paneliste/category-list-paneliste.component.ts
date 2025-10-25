import { Component } from '@angular/core';
import { Category } from '../categoryModel/category';
import { CategoryService } from '../categoryService/category.service';
import { Router,RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-category-list-paneliste',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category-list-paneliste.component.html',
  styleUrl: './category-list-paneliste.component.css'
})
export class CategoryListPanelisteComponent {

categoryList: Category[] = [];
  
    constructor(private categoryService: CategoryService, public router: Router) {}
  
    ngOnInit(): void {
      this.loadCategories();
    }
  
    loadCategories(): void {
      this.categoryService.getcategorieList().subscribe({
        next: (data) => {
          this.categoryList = data;
        },
        error: (err) => {
          console.error('Erreur lors du chargement des categories', err);
        }
      });
    }

}

