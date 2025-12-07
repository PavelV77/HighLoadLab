import { Component } from '@angular/core';
import { User } from '../user';
import { ActivatedRoute, Router } from '@angular/router';
import { UserServiceService } from '../user-service.service';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrl: './edit-user.component.css'
})
export class EditUserComponent {
  id: string = "";
  user: User = { id: "1cb23e39-ee49-46f5-8592-91c6ab32ef97", login: "", insertAt: 0, updateAt: 0 };

  constructor(private route: ActivatedRoute, private userService: UserServiceService, private router: Router) {}
  ngOnInit(){
    this.id = String(this.route.snapshot.paramMap.get('id'));
    if (this.id != "null") {
      this.userService.getUser(this.id).subscribe(data => this.user = data);
    }
  }
  save(){
    this.userService.updateUser(this.id, this.user).subscribe();
    this.router.navigate(["/users"])
  }
}
