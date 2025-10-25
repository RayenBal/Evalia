import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'timeAgo', standalone: true })
export class TimeAgoPipe implements PipeTransform {
  transform(value?: string | Date): string {
    if (!value) return '';
    const d = typeof value === 'string' ? new Date(value) : value;
    const s = Math.max(1, Math.floor((Date.now() - d.getTime()) / 1000));

    const steps: [number, string][] = [
      [60, 'seconde'], [60, 'minute'], [24, 'heure'],
      [7, 'jour'], [4.345, 'semaine'], [12, 'mois'], [1e9, 'an']
    ];

    let amt = s, i = 0;
    while (i < steps.length - 1 && amt >= steps[i][0]) { amt /= steps[i][0]; i++; }
    amt = Math.floor(amt);
    const label = steps[i][1] + (amt > 1 ? (i <= 2 ? 's' : 's') : '');
    return `il y a ${amt} ${label}`;
  }
}
