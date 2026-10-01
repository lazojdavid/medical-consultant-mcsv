import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { SesionService } from '../../core/login/services/sesion-service';
import { Appointment, AppointmentsService, ScheduleAppointment } from './appointments.service';

type DashboardSection = 'appointments' | 'consultations' | 'patients';
type AppointmentDialog = 'create' | 'reschedule' | 'cancel' | null;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly router = inject(Router);
  private readonly sesionService = inject(SesionService);
  private readonly appointmentsService = inject(AppointmentsService);

  protected readonly activeSection = signal<DashboardSection>('appointments');
  protected readonly appointments = signal<Appointment[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSaving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly dialogMode = signal<AppointmentDialog>(null);
  protected readonly editingAppointment = signal<Appointment | null>(null);
  protected readonly scheduledCount = () =>
    this.appointments().filter((appointment) => appointment.status === 'SCHEDULED').length;

  protected filterType: 'patient' | 'doctor' = 'patient';
  protected filterId = '';
  protected cancelReason = '';
  protected draft: ScheduleAppointment = this.emptyDraft();

  protected selectSection(section: DashboardSection): void {
    this.activeSection.set(section);
    this.errorMessage.set(null);
  }

  protected loadAppointments(): void {
    const entityId = Number(this.filterId);
    if (!Number.isInteger(entityId) || entityId < 1) {
      this.errorMessage.set('Ingresa un identificador válido para consultar las citas.');
      return;
    }

    this.errorMessage.set(null);
    this.isLoading.set(true);
    const request = this.filterType === 'patient'
      ? this.appointmentsService.findByPatient(entityId)
      : this.appointmentsService.findByDoctor(entityId);

    request.pipe(finalize(() => this.isLoading.set(false))).subscribe({
      next: (appointments) => this.appointments.set(appointments),
      error: () => this.errorMessage.set('No fue posible cargar las citas. Verifica que mcsv-citas esté disponible y que tu rol tenga acceso.'),
    });
  }

  protected openCreateDialog(): void {
    this.draft = this.emptyDraft();
    this.editingAppointment.set(null);
    this.dialogMode.set('create');
  }

  protected openRescheduleDialog(appointment: Appointment): void {
    this.draft = {
      patientId: appointment.patientId,
      doctorId: appointment.doctorId,
      startsAt: appointment.startsAt.slice(0, 16),
      endsAt: appointment.endsAt.slice(0, 16),
      reason: appointment.reason ?? '',
    };
    this.editingAppointment.set(appointment);
    this.dialogMode.set('reschedule');
  }

  protected openCancelDialog(appointment: Appointment): void {
    this.editingAppointment.set(appointment);
    this.cancelReason = '';
    this.dialogMode.set('cancel');
  }

  protected closeDialog(): void {
    this.dialogMode.set(null);
    this.editingAppointment.set(null);
  }

  protected saveAppointment(): void {
    const mode = this.dialogMode();
    const selected = this.editingAppointment();
    if (!mode) return;

    if (mode === 'reschedule' && selected) {
      this.submitMutation(this.appointmentsService.reschedule(selected.id, this.draft.startsAt, this.draft.endsAt));
      return;
    }

    if (mode === 'cancel' && selected) {
      this.submitMutation(this.appointmentsService.cancel(selected.id, this.cancelReason));
      return;
    }

    this.submitMutation(this.appointmentsService.schedule(this.draft));
  }

  protected formatDate(value: string): string {
    return new Date(value).toLocaleString('es-MX', { dateStyle: 'medium', timeStyle: 'short' });
  }

  protected trackById(_index: number, appointment: Appointment): number {
    return appointment.id;
  }

  logout(): void {
    this.sesionService.clearSession();
    this.router.navigateByUrl('/login');
  }

  private submitMutation(request: import('rxjs').Observable<Appointment>): void {
    this.errorMessage.set(null);
    this.isSaving.set(true);
    request.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: (appointment) => {
        const mode = this.dialogMode();
        if (mode === 'create' && this.filterId) {
          this.closeDialog();
          this.loadAppointments();
          return;
        }

        const current = this.appointments();
        const exists = current.some((item) => item.id === appointment.id);
        this.appointments.set(exists
          ? current.map((item) => item.id === appointment.id ? appointment : item)
          : [...current, appointment]);
        this.closeDialog();
      },
      error: (error: HttpErrorResponse) => {
        const backendMessage = error.error?.mensaje ?? error.error?.message;
        if (backendMessage === "The appointment is outside the doctor's availability.") {
          this.errorMessage.set('La cita está fuera del horario disponible del médico. Revisa el día y que todo el intervalo quede dentro de una disponibilidad activa.');
          return;
        }

        this.errorMessage.set(backendMessage ?? 'No fue posible guardar los cambios. Revisa los datos y los permisos de tu usuario.');
      },
    });
  }

  private emptyDraft(): ScheduleAppointment {
    return { patientId: 0, doctorId: 0, startsAt: '', endsAt: '', reason: '' };
  }
}
