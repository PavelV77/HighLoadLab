import { NgModule } from '@angular/core';
import { ActivatedRoute, Route, Router, RouterModule, Routes } from '@angular/router';
import { NewsListComponent } from './news-list/news-list.component';
import { UserListComponent } from './user-list/user-list.component';
import { EditNewsComponent } from './edit-news/edit-news.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { CommentListComponent } from './comment-list/comment-list.component';
import { EditCommentComponent } from './edit-comment/edit-comment.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AuthGuard } from './auth.guard';

const routes: Routes = [
  {path:"", redirectTo: "/news", pathMatch: "full"},
  {path:"auth/login", component: LoginComponent},
  {path:"auth/register", component: RegisterComponent},
  {path:"login", redirectTo: "/auth/login", pathMatch: "full"},
  {path:"register", redirectTo: "/auth/register", pathMatch: "full"},
  {path:"news", component: NewsListComponent, canActivate: [AuthGuard]},
  {path:"users", component: UserListComponent, canActivate: [AuthGuard]},
  {path:"news/:id/edit", component: EditNewsComponent, canActivate: [AuthGuard]},
  {path:"news/new", component: EditNewsComponent, canActivate: [AuthGuard]},
  {path:"users/:id/edit", component: EditUserComponent, canActivate: [AuthGuard]},
  {path:"users/new", component: EditUserComponent, canActivate: [AuthGuard]},
  {path:"news/:id/comments", component: CommentListComponent, canActivate: [AuthGuard]},
  {path:"news/:id/comments/new", component: EditCommentComponent, canActivate: [AuthGuard]},
  {path:"news/:id/comments/:commentId/edit", component: EditCommentComponent, canActivate: [AuthGuard]}

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule]
})
export class AppRoutingModule { 

}
