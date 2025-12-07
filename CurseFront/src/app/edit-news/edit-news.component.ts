import { Component, Input } from '@angular/core';
import { News } from '../news';
import { NewsServiceService } from '../news-service.service';
import { UUID } from 'crypto';
import { ActivatedRoute, Router } from '@angular/router';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-edit-news',
  templateUrl: './edit-news.component.html',
  styleUrl: './edit-news.component.css'
})
export class EditNewsComponent {

  constructor(private newsService: NewsServiceService, private route: ActivatedRoute, private router: Router) { }
  @Input() id: string = "";
  news: News = {
    id: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
    head: "",
    body: "",
    userId: "1cb23e39-ee49-46f5-8592-91c6ab32ef97",
    insertAt: 0,
    updateAt: 0,
    countLike: 0,
    countDislike: 0
  }

  ngOnInit() {
    this.id = String(this.route.snapshot.paramMap.get('id'));
    if (this.id != "null") {
      this.newsService.getNews(this.id).subscribe(data => this.news = data);
    }
  }


  save(): void {
    if (this.id == "null") {
      this.newsService.saveNews("1cb23e39-ee49-46f5-8592-91c6ab32ef97", this.news).subscribe(data => this.news = data);
    }
    else { 
      this.newsService.updateNews(this.id, this.news).subscribe(data => this.news = data);
    }
    this.router.navigate(["/news"]);
  }


}
