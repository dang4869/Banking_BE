package com.me.base.controller;

import com.me.base.dto.BaseResponse;
import com.me.base.dto.UserResponseDTO;
import com.me.base.service.IMessageService;
import com.me.base.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller dành cho Quản trị viên (ADMIN) để quản lý danh sách khách hàng (User).
 * <p>
 * Base path: /api/admin/users
 * Cần role ADMIN.
 */
@Tag(name = "Admin - Users", description = "Quản lý khách hàng dành cho Admin")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final IUserService userService;
    private final IMessageService messageService;

    @Operation(summary = "Lấy danh sách tất cả khách hàng", description = "Lấy tất cả user/khách hàng trong hệ thống")
    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponseDTO>>> getAllUsers() {
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.users.retrieved"), userService.getAllUsers()));
    }

    @Operation(summary = "Xem chi tiết một khách hàng", description = "Lấy chi tiết user theo ID")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.user.retrieved"), userService.getUserById(id)));
    }

    @Operation(summary = "Khóa tài khoản khách hàng", description = "Disable một khách hàng đang hoạt động")
    @PutMapping("/{id}/lock")
    public ResponseEntity<BaseResponse<String>> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok(BaseResponse.success("Khóa tài khoản thành công", null));
    }

    @Operation(summary = "Mở khóa tài khoản khách hàng", description = "Enable lại khách hàng bị khóa")
    @PutMapping("/{id}/unlock")
    public ResponseEntity<BaseResponse<String>> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(BaseResponse.success("Mở khóa tài khoản thành công", null));
    }
}
