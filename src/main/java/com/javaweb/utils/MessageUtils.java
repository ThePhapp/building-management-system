package com.javaweb.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageUtils {

	public Map<String, String> getMessage(String message) {
		Map<String, String> result = new HashMap<>();
		if (message.equals("update_success")) {
			result.put("message", "Cập nhật thành công.");
			result.put("alert", "success");
		} else if (message.equals("insert_success")) {
			result.put("message", "Tạo mới thành công.");
			result.put("alert", "success");
		} else if (message.equals("delete_success")) {
			result.put("message", "Vô hiệu hóa thành công.");
			result.put("alert", "success");
		} else if (message.equals("building_delete_success")) {
			result.put("message", "Đã xóa tòa nhà thành công.");
			result.put("alert", "success");
		} else if (message.equals("error_system")) {
			result.put("message", "Hệ thống chưa thể xử lý yêu cầu.");
			result.put("alert", "danger");
		} else if (message.equals("change_password_fail")) {
			result.put("message", "Mật khẩu hiện tại không đúng.");
			result.put("alert", "danger");
		} else if (message.equals("password_policy_fail")) {
			result.put("message", "Mật khẩu mới phải có ít nhất 8 ký tự và phần xác nhận phải trùng khớp.");
			result.put("alert", "danger");
		} else if (message.equals("reset_password_success")) {
			result.put("message", "Đặt lại mật khẩu thành công.");
			result.put("alert", "success");
		} else if (message.equals("role_require")) {
			result.put("message", "Vui lòng chọn vai trò.");
			result.put("alert", "danger");
		}
		return result;
	}
}
