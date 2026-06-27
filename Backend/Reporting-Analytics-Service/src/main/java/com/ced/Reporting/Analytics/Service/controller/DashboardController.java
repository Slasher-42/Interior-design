package com.ced.Reporting.Analytics.Service.controller;

import com.ced.Reporting.Analytics.Service.dto.AdminDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.ClientDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.DesignerDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.ProjectManagerDashboardResponse;
import com.ced.Reporting.Analytics.Service.dto.SalesDashboardResponse;
import com.ced.Reporting.Analytics.Service.security.CurrentUser;
import com.ced.Reporting.Analytics.Service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Each endpoint reads the role and userId straight from the caller's JWT and filters the
 * response accordingly, so the frontend calls a single endpoint per role and gets exactly
 * what that role should see - no client-side filtering of a larger payload.
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> admin(@RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(dashboardService.adminDashboard(authorization));
    }

    @GetMapping("/project-manager")
    public ResponseEntity<ProjectManagerDashboardResponse> projectManager() {
        return ResponseEntity.ok(dashboardService.projectManagerDashboard(CurrentUser.id()));
    }

    @GetMapping("/designer")
    public ResponseEntity<DesignerDashboardResponse> designer() {
        return ResponseEntity.ok(dashboardService.designerDashboard(CurrentUser.id()));
    }

    @GetMapping("/client")
    public ResponseEntity<ClientDashboardResponse> client() {
        return ResponseEntity.ok(dashboardService.clientDashboard(CurrentUser.id()));
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesDashboardResponse> sales() {
        return ResponseEntity.ok(dashboardService.salesDashboard());
    }
}
