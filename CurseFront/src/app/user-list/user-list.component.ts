import { Component } from '@angular/core';
import { User } from '../user';
import { UserServiceService } from '../user-service.service';
import { Router } from '@angular/router';
import { UUID } from 'crypto';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent {
  userList: User[] = [];
  constructor(private userService: UserServiceService, private router: Router) {}

  ngOnInit(){
    this.userService.getAll().subscribe(data => this.userList = data);
  }
  update(id: UUID): void{
    this.router.navigate(["users/"+id+"/edit"]);
  }
  delete(id: UUID): void{
    this.userService.deleteUser(id).pipe(switchMap(()=>this.userService.getAll())).subscribe(data => this.userList = data);
  }
}
