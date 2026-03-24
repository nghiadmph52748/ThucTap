# Swagger Test Evidence (Manual)

Mục tiêu: dùng Swagger UI để test API và ghi lại evidence (ảnh/chụp màn hình) cho báo cáo.

## 1) Mở Swagger UI

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
  - Nếu bạn chạy app port khác (vd 8086) thì thay bằng: `http://localhost:8086/swagger-ui/index.html`

## 2) Lấy JWT token qua login

1. Vào **Auth** -> `POST /api/auth/login`
2. Request body (ví dụ):

```json
{
  "email": "alice@gmail.com",
  "password": "<your_password>"
}
```

3. Execute và chụp evidence:
- **Evidence 1**: response `200` + trường `data.token`.

## 3) Nhập Bearer token vào Swagger (Authorize)

1. Bấm nút **Authorize** (góc phải trên Swagger UI)
2. Ở mục `bearerAuth`, nhập:

```
Bearer <PASTE_YOUR_TOKEN_HERE>
```

3. Bấm **Authorize** -> **Close**

- **Evidence 2**: chụp màn hình popup Authorize đã nhập token hoặc trạng thái authorized.

> Lưu ý: nếu bạn không thấy nút **Authorize** hoặc không thấy scheme `bearerAuth`, hãy rebuild và restart app.

## 4) Test một vài API qua Swagger

Chọn tối thiểu 2 API để chứng minh token hoạt động.

Gợi ý:

- Projects -> `GET /api/projects/list`
  - Kỳ vọng: `200` nếu user có role phù hợp.

- Tasks -> `GET /api/tasks/my`
  - Kỳ vọng: `200` và trả danh sách task của chính bạn.

- Tasks -> `GET /api/tasks/detail/{id}`
  - Kỳ vọng: `200` nếu MANAGER hoặc USER đang xem task được assign cho mình.

- **Evidence 3**: chụp màn hình response `200` của ít nhất 1 API protected.
- **Evidence 4 (optional)**: chụp trường hợp bị chặn `403` khi gọi API yêu cầu MANAGER bằng token USER.

## 5) Ghi chú

- `/api/auth/**` là public.
- Các endpoint khác đa số cần đăng nhập và/hoặc role `MANAGER` (xem `SecurityConfig`).


