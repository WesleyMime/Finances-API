import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-value',
  templateUrl: './loading-value.component.html',
  styleUrl: './loading-value.component.css',
})
export class LoadingValueComponent {
  @Input() value: any;
  @Input() message = '';
  @Input() loadingImg = '/assets/loading-ezgif.gif';
}
