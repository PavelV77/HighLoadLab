import { Component, Input, Output, EventEmitter } from '@angular/core';
import { News } from '../news';
import { LikeServiceService } from '../like-service.service';
import { UUID, randomUUID } from 'crypto';


@Component({
  selector: 'app-news-shower',
  templateUrl: './news-shower.component.html',
  styleUrl: './news-shower.component.css'
})
export class NewsShowerComponent {
  @Input() news!: News;

  constructor(private likeService: LikeServiceService) { }

  @Output() likeEvent = new EventEmitter<UUID>();
  @Output() dislikeEvent = new EventEmitter<UUID>();
  @Output() editEvent = new EventEmitter<UUID>();
  @Output() deleteEvent = new EventEmitter<UUID>();
  @Output() commentEvent = new EventEmitter<UUID>();
  isLike: boolean = false;

  like(): void {
    if(!this.isLike){
      this.likeEvent.emit(this.news.id);
      this.isLike=true;
    }
  }

  disLike(): void {
    this.dislikeEvent.emit(this.news.id);
  }

  edit(): void {

    this.editEvent.emit(this.news.id);
  }
  delete(): void {
    this.deleteEvent.emit(this.news.id)
  }

  comment(): void {
    this.commentEvent.emit(this.news.id)
  }
}
