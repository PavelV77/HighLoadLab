import { Component, Output, EventEmitter, Input} from '@angular/core';
import { UUID } from 'crypto';
import { User } from '../user';


@Component({
  selector: 'app-user-shower',
  templateUrl: './user-shower.component.html',
  styleUrl: './user-shower.component.css'
})
export class UserShowerComponent {
  @Input() user!: User;
  @Output() editEvent = new EventEmitter<UUID>();
  @Output() deleteEvent = new EventEmitter<UUID>();

  update(): void {
    this.editEvent.emit(this.user.id);
  }

  delete(): void {
    this.deleteEvent.emit(this.user.id);
  }
}
