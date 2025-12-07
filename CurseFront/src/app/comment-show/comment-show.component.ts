import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentDto } from '../comment-dto';
import { UUID } from 'crypto';

@Component({
  selector: 'app-comment-show',
  templateUrl: './comment-show.component.html',
  styleUrl: './comment-show.component.css'
})
export class CommentShowComponent {
  @Input() comment!: CommentDto;
  @Output() editEvent = new EventEmitter<CommentDto>();
  @Output() deleteEvent = new EventEmitter<CommentDto>();

  delete(){
    this.deleteEvent.emit(this.comment);
  }

  update(){
    this.editEvent.emit(this.comment);
  }

}
