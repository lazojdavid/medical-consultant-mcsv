import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Appointment {
  id: number;
  patientId: number;
  doctorId: number;
  startsAt: string;
  endsAt: string;
  status: 'SCHEDULED' | 'CANCELLED';
  reason: string | null;
  cancellationReason: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ScheduleAppointment {
  patientId: number;
  doctorId: number;
  startsAt: string;
  endsAt: string;
  reason: string;
}

@Injectable({ providedIn: 'root' })
export class AppointmentsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.citasApiUrl}/appointments`;

  findByPatient(patientId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  findByDoctor(doctorId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.baseUrl}/doctor/${doctorId}`);
  }

  schedule(request: ScheduleAppointment): Observable<Appointment> {
    return this.http.post<Appointment>(this.baseUrl, request);
  }

  reschedule(id: number, startsAt: string, endsAt: string): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.baseUrl}/${id}/reschedule`, { startsAt, endsAt });
  }

  cancel(id: number, reason: string): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.baseUrl}/${id}/cancel`, { reason });
  }
}