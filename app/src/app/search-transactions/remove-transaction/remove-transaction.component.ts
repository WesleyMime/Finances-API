import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-remove-transaction',
  imports: [],
  templateUrl: './remove-transaction.component.html',
  styleUrl: './remove-transaction.component.css'
})
export class RemoveTransactionComponent {
    // Output events to notify the parent of the user's choice
    @Output() cancelled = new EventEmitter<void>();
    @Output() confirmed = new EventEmitter<void>();
  
    onCancel(): void {
      this.cancelled.emit();
    }
  
    onConfirm(): void {
      this.confirmed.emit();
    }
}
