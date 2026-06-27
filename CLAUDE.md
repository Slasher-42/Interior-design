# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.


Interior Design Service Management System
System Overview
Interior design firms often face challenges in managing clients, service requests, project timelines, quotations, team coordination, documentation, and customer feedback. Traditional manual processes can result in communication gaps, delayed project delivery, poor project visibility, and inefficient resource utilization.
This project proposes the development of an Interior Design Service Management System (IDSMS), a centralized digital platform designed to automate and streamline the operational processes of interior design companies. The system will facilitate client management, service request handling, quotation generation, project planning, task assignment, document management, communication, feedback collection, and business analytics.
The platform aims to improve operational efficiency, enhance collaboration between clients and design teams, provide real-time project monitoring, and support management decision-making through comprehensive reporting and analytics.

Objectives
General Objective
To design and implement a digital platform that streamlines and automates interior design service operations, improving project management, customer satisfaction, and organizational efficiency.
Specific Objectives
Develop a centralized client and project management system. 
Automate service request submission and tracking. 
Facilitate quotation and proposal generation for interior design projects. 
Improve project planning, scheduling, and progress monitoring. 
Enable efficient task assignment and resource management. 
Manage design portfolios and project documentation digitally. 
Enhance communication between clients and design teams. 
Collect and analyze customer feedback and satisfaction ratings. 
Generate analytical reports for strategic decision-making. 
Ensure data security through role-based access control and audit tracking. 

UI Modules & Features
1. User Registration & Authentication Module
UI Elements
User registration form 
Role selection (Administrator, Project Manager, Designer, Sales Team, Client) 
Full name, email, phone number fields 
Password creation with strength checker 
Email verification interface 
Login page 
Forgot password functionality 
Password reset page 
User profile management 
Session timeout controls 
Login activity logs 
Multi-factor authentication setup 
Features
Secure user registration and authentication 
Role-based access control 
User profile management 
Password recovery and account verification 
Session monitoring 
Login history tracking 
Multi-factor authentication 

2. Dashboard Module
UI Elements
Personalized dashboard based on user role 
Total clients card 
Active projects card 
Pending quotations card 
Ongoing tasks card 
Customer satisfaction score 
Recent activity feed 
Upcoming project deadlines 
Notifications panel 
Project progress charts 
Revenue and performance statistics 
Features
Real-time project overview 
Personalized workspace 
Performance monitoring 
Quick access to frequently used functions 
Project and service insights 

3. Client Management Module
UI Elements
Client registration form 
Client profile page 
Client list table 
Search and filter options 
Client interaction history 
Contact management 
Client project history 
Communication records 
Features
Client information management 
Customer relationship tracking 
Contact history management 
Project history monitoring 
Client segmentation 

4. Service Request Management Module
UI Elements
Service request submission form 
Request category selection 
Priority assignment 
Request status tracker 
Request approval workflow 
Assigned staff details 
Request history viewer 
Search and filter functionality 
Features
Request submission and management 
Automated request assignment 
Request prioritization 
Request status tracking 
Service workflow automation 

5. Quotation & Proposal Management Module
UI Elements
Cost estimation form 
Material selection interface 
Labor cost calculator 
Quotation generation screen 
Proposal approval workflow 
PDF quotation export 
Quotation history viewer 
Client approval status tracking 
Features
Automated quotation generation 
Cost estimation 
Proposal management 
Quotation approval process 
PDF document generation 

6. Project Management Module
UI Elements
Project creation form 
Project timeline view 
Milestone management 
Progress tracker 
Budget monitoring dashboard 
Project status indicators 
Calendar integration 
Project history page 
Features
Project planning and scheduling 
Timeline management 
Budget tracking 
Milestone monitoring 
Progress reporting 
Project lifecycle management 

7. Task & Resource Management Module
UI Elements
Task creation form 
Task assignment panel 
Resource allocation dashboard 
Team schedule calendar 
Task priority indicators 
Task progress tracker 
Workload distribution chart 
Deadline reminders 
Features
Task assignment and monitoring 
Team scheduling 
Resource allocation 
Workload balancing 
Deadline management 

8. Design Portfolio & Documentation Module
UI Elements
File upload interface 
Design gallery 
Project document repository 
File version management 
Search and filter tools 
Folder organization 
Preview and download options 
Approval workflow interface 
Features
Secure document storage 
Portfolio management 
Design file sharing 
Version control 
Document approval tracking 

9. Customer Feedback & Review Module
UI Elements
Customer satisfaction survey 
Project rating form 
Feedback submission page 
Complaint management screen 
Review dashboard 
Feedback history viewer 
Analytics charts 
Features
Feedback collection 
Customer satisfaction tracking 
Complaint management 
Review analysis 
Service quality monitoring 

10. Communication & Notification Module
UI Elements
Notification center 
Internal messaging interface 
Project update alerts 
Email notification settings 
Client communication logs 
Chat interface 
Broadcast announcement panel 
Reminder settings 
Features
Real-time notifications 
Internal communication 
Client messaging 
Project update alerts 
Automated reminders 
Communication history 

11. Reporting & Analytics Module
UI Elements
Executive dashboard 
KPI cards 
Project performance reports 
Revenue analysis charts 
Customer satisfaction reports 
Service request reports 
Employee productivity reports 
Custom report builder 
Export options (PDF/Excel) 
Features
Business intelligence reporting 
Project analytics 
Customer analytics 
Revenue monitoring 
Performance measurement 
Data-driven decision support 

12. Resource & Vendor Management Module
UI Elements
Vendor registration form 
Supplier directory 
Material inventory list 
Material request form 
Purchase order creation 
Vendor performance dashboard 
Material cost tracker 
Inventory status dashboard 
Features
Vendor management 
Material procurement tracking 
Inventory monitoring 
Purchase order management 
Cost control and analysis 

13. Admin & System Management Module
UI Elements
User management dashboard 
Role and permission configuration 
System settings page 
Backup management interface 
Audit logs viewer 
Data management controls 
System health dashboard 
Maintenance mode settings 
Features
User administration 
Permission management 
System configuration 
Backup and recovery 
Audit trail monitoring 
System maintenance 

14. Security & Audit Module
UI Elements
Role permission matrix 
Login activity monitor 
Audit log viewer 
User action history 
Data access reports 
Security alerts dashboard 
Password policy settings 
Two-factor authentication settings 
Features
Data security and protection 
Audit trail management 
Access monitoring 
Security incident tracking 
User activity logging 
Compliance support





Service 1 — Auth & Security Service
Framework: Spring Boot
This service is the entry point of the entire system and every other service depends on it. No request reaches any part of the platform without first passing through here. When a new user wants to join the platform, they fill in their full name, email, phone number, password, and select their role — Administrator, Project Manager, Designer, Sales Team, or Client. Regular users like Designers and Sales Team members can self-register, but Administrator accounts can only be created manually by a super admin to prevent unauthorized privilege escalation. Once the registration form is submitted, the service hashes the password using BCrypt, saves the credentials to its own isolated database, generates an email verification token, and immediately publishes a user.registered event to Kafka carrying the userId, email, full name, role, and creation timestamp. The User & Client Service consumes this event to build a full profile, the Feedback & Communication Service consumes it to send the verification email, and the Reporting & Analytics Service consumes it to update user count metrics on the admin dashboard.
After the user clicks the verification link, the Auth & Security Service marks the account as verified and publishes a user.verified event, which the Feedback & Communication Service consumes to send a welcome message. Login itself is a pure synchronous REST operation — the user submits their email and password, the service validates them against the stored hash, checks whether multi-factor authentication is enabled, and if everything passes it issues a signed JWT token containing the userId, role, and permissions. This token is what every other service uses to authorize incoming requests independently without making a call back to this service on every operation. The service also handles the full password reset flow — a time-limited reset link is emailed to the user, they click it, set a new password, and the old credentials are invalidated. If a user enters wrong credentials multiple times, the account is temporarily locked to block brute force attempts. Every login attempt, password reset, MFA verification, and account lock event is written to an audit log table that lives inside this service and is also published as Kafka events for the Reporting & Analytics Service to track security activity across the platform.
This service also owns all system-level administration. A system settings table stores platform-wide configurations including password complexity policy, session timeout duration, and the MFA enforcement toggle — all manageable by the super admin through a /admin/settings endpoint. A scheduled background job runs at defined intervals to trigger database snapshots, writing each backup event to the audit log so there is a complete history of when backups occurred and whether they succeeded. System health is exposed through a /admin/health endpoint powered by Spring Boot Actuator, surfacing uptime, memory usage, and database connection status in real time. A maintenance mode flag lives in the system settings table — when the super admin enables it, the Auth service rejects all login attempts except for the super admin account and returns a clear maintenance message to any other user who tries to log in.

Service 2 — User & Client Service
Framework: Spring Boot
This service is responsible for everything that has to do with user identity beyond raw credentials, and for the full lifecycle of client records on the platform. It consumes the user.registered Kafka event published by the Auth & Security Service and uses it to create a rich profile record for the new user in its own database. Depending on the role in the event payload, it creates the appropriate identity profile — for a Designer it stores professional title, area of specialization, country, and city; for a Sales Team member it stores department and contact details; for a Client it stores organization name, industry, country, city, website, and contact details; for a Project Manager it stores their department and assigned projects. Every user can update their own full name, phone number, and profile image through their profile page, and can change their password by providing their current password first as verification.
When an Admin or Sales Team member adds a client directly through the platform, this service saves the client record and publishes a client.created event to Kafka containing the clientId, name, email, phone, and creation timestamp. The Feedback & Communication Service consumes this to send the client a welcome message, and the Reporting & Analytics Service consumes it to update the total client count on the dashboard. This service also maintains the full interaction history for each client — every service request they submit, every project linked to their account, and every communication thread they are part of gets logged here so that any team member with access can open a client profile and see the complete relationship history in one place. The Admin has full control through a user management panel to search, filter by role, activate, deactivate, or permanently delete any user account. A deactivated user cannot log in even with correct credentials and sees a clear message explaining the situation. When a user is deleted, this service publishes a user.deleted event to Kafka and all other services that hold references to that user handle cleanup according to their own logic.
This service also supports client segmentation. Since client records already store industry, country, and city, the service exposes a GET /clients endpoint that accepts optional filter parameters — industry, country, and city — so Sales Team members can query and group clients by any combination of those dimensions. A dedicated GET /clients/segments/summary endpoint returns client counts grouped by industry and region, feeding the analytics dashboard with segmentation data that helps management understand the client base distribution without scanning individual records.

Service 3 — Service Request & Quotation Service
Framework: Spring Boot
This service handles two tightly related domains — the submission and tracking of service requests, and the generation and approval of quotations tied to those requests. These two live together because a quotation cannot exist without a service request, and a project cannot be created without an approved quotation, making them a natural pipeline that belongs in one service. When a client logs in and submits a service request describing what they need — for example a full living room redesign — they fill in the request category, describe the scope of work, and set a priority level. The service saves the request with a status of Pending and immediately publishes a service.request.created event to Kafka carrying the requestId, clientId, category, priority, description, and submission timestamp. The Feedback & Communication Service consumes this to notify the Sales Team and Admin that a new request has arrived. The User & Client Service consumes it to log the request under the client's interaction history. The Reporting & Analytics Service consumes it to increment the pending requests counter on the dashboard.
When the Sales Team reviews the request and assigns it to a Designer, the service updates the status to Assigned and publishes a service.request.assigned event containing the requestId, clientId, assigned designer ID, and assignment timestamp. The Feedback & Communication Service consumes this to notify both the assigned designer and the client. The Project & Task Service consumes it to prepare for receiving the project that will be linked to this request. Next, the Sales Team creates a quotation for the request — they enter material costs, labor costs, and any additional charges, and the service calculates the total and generates a PDF. It then publishes a quotation.created event to Kafka with the quotationId, requestId, clientId, total amount, and a status of Pending Approval. The Feedback & Communication Service consumes this to email the PDF to the client. The Reporting & Analytics Service consumes it to log the pending quotation value. When the client reviews and approves the quotation, the service updates the status to Approved and publishes a quotation.approved event. The Project & Task Service consumes this as the trigger to automatically create the project. The Feedback & Communication Service consumes it to notify the project manager and design team. The Reporting & Analytics Service consumes it to move the value into the active revenue pipeline.

Service 4 — Project & Task Service
Framework: Spring Boot
This service is the operational core of the platform. It manages everything that happens from the moment a project is created until it is marked complete. It consumes the quotation.approved Kafka event from the Service Request & Quotation Service and uses it to automatically create a project record in its own database, populated with the clientId, requestId, quotationId, assigned project manager, start date, end date, and approved budget. Once the project is created, the service publishes a project.created event to Kafka carrying all these details. The User & Client Service consumes this to link the project to the client's history. The Feedback & Communication Service consumes it to notify all stakeholders. The Reporting & Analytics Service consumes it to add the project to the active projects counter on the dashboard.
With the project live, the project manager sets up milestones and creates tasks, assigning each task to a specific designer or team member with a priority level and a deadline. Every time a task is assigned, the service publishes a task.assigned event to Kafka with the taskId, projectId, assigned user ID, title, priority, and deadline. The Feedback & Communication Service consumes this to notify the team member. The Reporting & Analytics Service consumes it to track workload distribution per employee. When a task is marked as completed by the assigned person, the service publishes a task.completed event. The service itself also consumes this event internally to check whether all tasks under a milestone are done and automatically advances the milestone status if they are. The Feedback & Communication Service consumes it to notify the project manager. The Reporting & Analytics Service consumes it to update the project progress percentage visible on dashboards. When all milestones are done and the project manager marks the project as Completed, the service publishes a project.completed event carrying the projectId, clientId, completion timestamp, and final cost. The Feedback & Communication Service consumes this to notify the client and trigger a feedback request. The Reporting & Analytics Service consumes it to move the project to the completed column and log the final revenue figure.

Service 5 — Document & Portfolio Service
Framework: Spring Boot
This service handles all file storage, design portfolio management, and document version control for the platform. Designers upload files — mood boards, floor plans, design drafts, rendered visuals, and any other project documents — directly through REST endpoints in this service using multipart file upload. Each file is stored securely and associated with a specific project. The service maintains version history for every document so that when a designer uploads a revised version of a file, the previous version is retained and accessible rather than overwritten. The service also provides a design gallery view where all approved assets for a project can be browsed and downloaded by authorized parties including the client and the project manager.
When a project manager reviews a submitted design file and marks it as approved, the service publishes a document.approved event to Kafka carrying the documentId, projectId, the ID of who uploaded it, the ID of who approved it, and the approval timestamp. The Feedback & Communication Service consumes this event to notify the client that a new design is ready for their review. The Reporting & Analytics Service consumes it to log document activity per project for productivity tracking. The service also enforces an approval workflow — documents go through a Submitted, Under Review, Approved, or Rejected status cycle, and only approved documents are made visible to the client. Rejected documents are returned to the designer with a written reason so they can revise and resubmit. The service also supports folder organization so that large projects with many files can be structured clearly, and it provides preview and download options for all stored files without requiring the user to leave the platform.

Service 6 — Vendor & Inventory Service
Framework: Spring Boot
This service manages everything related to material sourcing, vendor relationships, and procurement tracking. Project managers and Admin staff can register vendors through a vendor registration form, building a supplier directory that stores the vendor name, contact details, supplied materials, and performance history. When a project requires materials, the project manager submits a material request through this service specifying the materials needed, the quantities, and the target project. The service matches the request against registered vendors and allows the project manager to create a purchase order for the selected vendor. When a purchase order is created, the service publishes a purchase.order.created event to Kafka carrying the orderId, projectId, vendorId, list of materials, estimated cost, and creation timestamp. The Project & Task Service consumes this event to track the material cost against the project's approved budget and flag any overruns. The Reporting & Analytics Service consumes it to log procurement spending. The Feedback & Communication Service consumes it to notify the Admin that a new purchase order has been raised and requires awareness.
The service also maintains an inventory dashboard that gives real-time visibility into material stock levels across active projects. When materials arrive, the inventory is updated and linked to the corresponding project. The service tracks vendor performance over time — delivery timeliness, order accuracy, and pricing consistency — through a vendor performance dashboard that helps management make informed procurement decisions. Material cost tracking within this service feeds budget monitoring on the Project & Task Service side through the Kafka event stream, ensuring that no project silently goes over budget due to procurement activity happening in isolation.

Service 7 — Feedback & Communication Service
Framework: Spring Boot with Spring Kafka
This service serves two functions that belong together — collecting feedback from clients and handling all communication and notifications across the platform. On the feedback side, when the service consumes a project.completed event from the Project & Task Service, it automatically creates an open feedback record for the client and sends them a feedback request. The client logs in and fills out a satisfaction survey rating the project overall, the design quality, the team responsiveness, and the timeline adherence. They can also submit written comments or file a complaint if they have concerns. When the client submits their feedback, the service saves it and publishes a feedback.submitted event to Kafka carrying the feedbackId, clientId, projectId, rating score, and submission timestamp. The Reporting & Analytics Service consumes this to update customer satisfaction averages on the executive dashboard. If the rating is below a defined threshold — for example three stars out of five — the Reporting & Analytics Service also triggers a flag on the analytics panel so management is alerted to investigate.
On the communication side, this service is the single consumer responsible for sending every notification and message across the platform. It consumes Kafka events from all other services and maps each one to the appropriate action — sending an email, pushing an in-platform notification, or triggering a chat alert. When user.registered is consumed it sends the verification email. When user.verified is consumed it sends the welcome message. When client.created is consumed it sends the client welcome message. When service.request.created is consumed it alerts the Sales Team and Admin. When service.request.assigned is consumed it notifies the assigned designer and the client. When quotation.created is consumed it emails the PDF to the client. When quotation.approved is consumed it alerts the project manager and design team. When project.created is consumed it notifies all stakeholders. When task.assigned is consumed it messages the assigned team member. When task.completed is consumed it notifies the project manager. When document.approved is consumed it tells the client a new design is ready for review. When purchase.order.created is consumed it notifies the Admin. When project.completed is consumed it messages the client and triggers the feedback flow. When feedback.submitted is consumed and the rating falls below the defined threshold it immediately alerts management. All in-platform notifications are stored per recipient and displayed through a notification bell in the platform header with an unread count badge. Communication logs per client are retained so any team member can open a client record and see the full history of messages and alerts sent to that client.

Service 8 — Reporting & Analytics Service
Framework: Spring Boot with Spring Kafka
This service is a pure consumer — it never exposes mutation endpoints and never publishes Kafka events of its own. Its entire job is to listen to events from every other service and maintain an aggregated, up-to-date picture of the business that management can query at any time through the analytics dashboard. It consumes user.registered to maintain a total and trend of user signups over time. It consumes client.created to track total client count and growth, and it pulls segmentation summaries from the User & Client Service to display client distribution by industry and region on the executive dashboard. It consumes service.request.created to count incoming requests and categorize them by type and priority. It consumes quotation.created and quotation.approved to track pending and converted quotation values. It consumes project.created, task.assigned, task.completed, and project.completed to build a full picture of project pipeline health — how many projects are active, what percentage of tasks are on track, how long projects take on average, and how final costs compare to budgets. It consumes purchase.order.created to aggregate procurement spending per project and overall. It consumes feedback.submitted to calculate and update customer satisfaction scores and flag low-rated projects for management review.
All of this data is pre-aggregated and stored in this service's own read-optimized database so that dashboard queries are fast and never put load on the operational services. The executive dashboard built on top of this service exposes role-scoped endpoints — GET /dashboard/admin returns the full KPI set, GET /dashboard/project-manager returns only the projects and tasks under that manager, GET /dashboard/designer returns only the tasks assigned to that designer, GET /dashboard/client returns only that client's project status and feedback history, and GET /dashboard/sales returns pending service requests and open quotations. Each endpoint reads the role and userId directly from the incoming JWT token and filters the response accordingly, so the frontend calls a single endpoint and receives exactly what that role should see. Below the KPI cards sit time-series charts for revenue trends, project completion rates, task throughput per team member, and procurement cost trends. Managers can also use a custom report builder to select dimensions and date ranges and export the result as a PDF or Excel file. Because this service only reads from Kafka events and writes to its own store, it is completely decoupled from every operational service and can be scaled, queried, or even rebuilt from the event log at any point without affecting the rest of the platform.