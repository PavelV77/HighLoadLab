import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NewsShowerComponent } from './news-shower/news-shower.component';
import { NewsListComponent } from './news-list/news-list.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { UserListComponent } from './user-list/user-list.component';
import { EditNewsComponent } from './edit-news/edit-news.component';
import { UserShowerComponent } from './user-shower/user-shower.component';
import { EditUserComponent } from './edit-user/edit-user.component';
import { CommentListComponent } from './comment-list/comment-list.component';
import { CommentShowComponent } from './comment-show/comment-show.component';
import { EditCommentComponent } from './edit-comment/edit-comment.component';

@NgModule({
  declarations: [
    AppComponent,
    NewsShowerComponent,
    NewsListComponent,
    UserListComponent,
    EditNewsComponent,
    UserShowerComponent,
    EditUserComponent,
    CommentListComponent,
    CommentShowComponent,
    EditCommentComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
