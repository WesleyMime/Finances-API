import { Component, EventEmitter, Output } from '@angular/core';
import { ToggleVisibilityService } from './toggle-visibility.service';

@Component({
  selector: 'app-hide-value',
  imports: [],
  templateUrl: './hide-value.component.html',
  styleUrl: './hide-value.component.css',
})
export class HideValueComponent {
  @Output() toggle = new EventEmitter<boolean>();
  hidden: boolean;
  constructor(private toggleVisibilityService: ToggleVisibilityService) {
    this.hidden = toggleVisibilityService.isHidden;
  }

  hide() {
    this.hidden = this.toggleVisibilityService.toggle();
    this.toggle.emit(this.toggleVisibilityService.isHidden);
  }
}
