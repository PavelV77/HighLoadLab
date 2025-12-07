import { NgModule } from '@angular/core';
import { ActivatedRoute, Route, Router, RouterModule, Routes } from '@angular/router';
import { NewsListComponent } from './news-list/news-list.component';
import { UserListComponent } from './user-list/user-list.component';
import { EditNewsComponent } from './edit-news/edit-news.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { CommentListComponent } from './comment-list/comment-list.component';
import { EditCommentComponent } from './edit-comment/edit-comment.component';

const routes: Routes = [
  {path:"news", component: NewsListComponent},
  {path:"users", component: UserListComponent},
  {path:"news/:id/edit", component: EditNewsComponent},
  {path:"news/new", component: EditNewsComponent},
  {path:"users/:id/edit", component: EditUserComponent},
  {path:"users/new", component: EditUserComponent},
  {path:"news/:id/comments", component: CommentListComponent},
  {path:"news/:id/comments/new", component: EditCommentComponent},
  {path:"news/:id/comments/:commentId/edit", component: EditCommentComponent}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { 

}
