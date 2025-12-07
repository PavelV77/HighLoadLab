import { Component, Input } from '@angular/core';
import { CommentDto } from '../comment-dto';
import { CommentServiceService } from '../comment-service.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-edit-comment',
  templateUrl: './edit-comment.component.html',
  styleUrl: './edit-comment.component.css'
})
export class EditCommentComponent {
  @Input() comment: CommentDto = {id: "1cb23e39-ee49-46f5-8592-91c6ab32ef97", body: "", insertAt: 0, updateAt: 0, newsId: "1cb23e39-ee49-46f5-8592-91c6ab32ef97", userId:"1cb23e39-ee49-46f5-8592-91c6ab32ef97"};
  commentId!: string;
  newsId!: string;

  constructor(private commentService: CommentServiceService, private route: ActivatedRoute, private router: Router){}


  ngOnInit(){
    this.commentId = String(this.route.snapshot.paramMap.get("commentId"));
    this.newsId = String(this.route.snapshot.paramMap.get("id"));
    if(this.commentId != "null") {
      this.commentService.getComment(this.commentId);
    }

  }
  save(): void {
    if(this.commentId != "null") {
      this.comment.userId = "1cb23e39-ee49-46f5-8592-91c6ab32ef97";
      this.commentService.updateComment(this.commentId, this.comment).subscribe(data => this.comment = data);
    }
    else {
      this.commentService.saveComment(this.newsId, this.comment).subscribe(data => this.comment = data);;
    }
    this.router.navigate(["/news/"+this.newsId+"/comments"])
  }
}
