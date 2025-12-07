import { Component, Input } from '@angular/core';
import { CommentDto } from '../comment-dto';
import { CommentServiceService } from '../comment-service.service';
import { UUID } from 'crypto';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, switchMap } from 'rxjs';
import { UserServiceService } from '../user-service.service';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrl: './comment-list.component.css'
})
export class CommentListComponent {

  constructor(private commentService: CommentServiceService, private userService: UserServiceService, private route: ActivatedRoute, private router: Router) { }

  @Input() newsId!: string;
  commentList: CommentDto[] = [];

  ngOnInit(): void {
    this.newsId = String(this.route.snapshot.paramMap.get("id"));
    if (this.newsId != "null") { 
      this.commentService.getAllForNews(this.newsId).subscribe(data => this.commentList = data);
    }
  }

  edit(comment: CommentDto): void {
    this.router.navigate(["news/"+comment.newsId+"/comments/"+comment.id+"/edit"]);

  }

  delete(comment: CommentDto): void {
    this.commentService.deleteComment(comment.id).pipe(switchMap(() => this.commentService.getAllForNews(this.newsId))).subscribe(data => this.commentList = data);
  }
}
