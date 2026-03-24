# Test Quality Review Note

Phạm vi: review các test hiện có trong module test, tập trung vào `TaskServiceTest` (unit) và `TaskRequestValidationTest` (validation).

## A) Note phân tích service cần test (TaskService)

File: `src/main/java/org/example/thuctapproject/service/TaskService.java`

### 1) Danh sách method & mức độ ưu tiên test

**Nhóm ưu tiên cao (có rule/logic + nhiều nhánh lỗi):**

1. `createTask(TaskRequest request)`
   - Map dữ liệu từ request sang entity.
   - Parse status (`parseStatus`).
   - Lookup `assignee` và `project` qua repository.
   - Ném `ApiException` theo nhiều điều kiện.

2. `assignTask(Integer taskId, Integer userId)`
   - Rule quan trọng: chỉ cho assign nếu user thuộc project của task (`projectRepository.existsByIdAndUsers_Id`).
   - Nhiều nhánh lỗi (null id, notfound, forbidden).

**Nhóm ưu tiên trung bình (có logic/rule nhưng ít hơn):**

3. `changeStatus(Integer taskId, String status)`
   - Rule: không cho change status nếu task đã `DONE`.
   - Parse status.
   - Lưu lại task.
   - Lưu ý: implementation hiện gọi `taskRepository.findById(taskId).get()` trước khi `orElseThrow` => rủi ro `NoSuchElementException` nếu taskId không tồn tại.

4. `updateTask(Integer id, TaskRequest request)`
   - Update title/status/assignee/project.
   - Validate null (assigneeId/projectId).
   - Not found cho task/assignee/project.

**Nhóm ưu tiên thấp (thin layer / ít logic):**

5. `getTaskByProjectId(Integer projectId)`, `getTaskByUserId(Integer userId)`, `getAllTask()`
   - Chủ yếu pass-through repo + mapping `TaskResponse`.
   - Có guard null cho projectId/userId.

6. `deleteTask(Integer id)`
   - Guard null + gọi `taskRepository.deleteById(id)`.

7. `getMyTasks()` và `getTaskById(Integer id)`
   - Phụ thuộc `SecurityContextHolder` và quyền `ROLE_MANAGER` (authorization rule).
   - Unit test được nhưng cần mock SecurityContext/Authentication.
   - Ưu tiên sau khi đã xong các rule business chính (`createTask`, `assignTask`).

### 2) Ma trận scenario cần cover (gợi ý testcase)

#### a) `createTask`

**Happy path**
- project tồn tại, status hợp lệ, có assignee hợp lệ => save 1 lần.
- project tồn tại, status hợp lệ, assignee = null => save 1 lần, assignee null.

**Error path**
- `request.project == null` => `ApiException` code `400`.
- `request.status == null` => `ApiException` code `400`.
- `request.status` invalid => `ApiException` (hiện code `404`).
- `request.assignee != null && > 0` nhưng user không tồn tại => `ApiException` `404`.
- project không tồn tại => `ApiException` `404`.

**Edge cases (nên bổ sung về sau)**
- `request.assignee = 0` hoặc `<0` => service bỏ qua lookup `userRepository.findById`.
- Verify mapping `deadline` (hiện service chưa set deadline vào entity).

#### b) `assignTask`

**Happy path**
- task tồn tại, user tồn tại, user thuộc project => set assignee + save 1 lần.

**Error path**
- `taskId == null` => `400`.
- `userId == null` => `400`.
- task không tồn tại => `404`.
- user không tồn tại => `404`.
- user không thuộc project => `403` và không gọi `save`.

#### c) `parseStatus` (private)

- Test gián tiếp qua `createTask`, `updateTask`, `changeStatus`.
- Case-insensitive, trim: `"in_progress"`, `"  todo "`.
- Invalid value => `ApiException`.

#### d) Phân loại loại test phù hợp

- `createTask`, `assignTask`, `updateTask`, `changeStatus`, `deleteTask`: **Unit test với Mockito** (mock repositories).
- `getMyTasks`, `getTaskById`: unit test vẫn làm được nhưng cần mock SecurityContext; hoặc có thể để integration test nếu muốn test security end-to-end.

---

## 1) Tổng quan bộ test hiện tại

- **Unit test (Mockito)**: `src/test/java/org/example/thuctapproject/service/TaskServiceTest.java`
  - Mục tiêu: test logic service độc lập với database bằng mock repository.
- **Validation test (Jakarta Validation)**: `src/test/java/org/example/thuctapproject/validation/TaskRequestValidationTest.java`
  - Mục tiêu: test constraint của DTO `TaskRequest`.
- **Spring context smoke test**: `ThucTapProjectApplicationTests`
  - Mục tiêu: đảm bảo context load OK (integration-level smoke).

## 2) Review chất lượng `TaskServiceTest`

### Điểm tốt

- **Isolation rõ ràng (đúng unit test)**
  - Dùng `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`.
  - Không khởi tạo Spring context, không phụ thuộc DB.

- **Coverage nhánh logic chính**
  - `createTask`:
    - Happy path với assignee + status parse.
    - Case không có assignee.
    - Case lỗi: project null (400), assignee not found (404), project not found (404), status null (400), status invalid (404).
  - `assignTask`:
    - Happy path: user thuộc project -> `save`.
    - Rule enforce: user không thuộc project -> 403 và **không save**.
    - Case lỗi: taskId/userId null (400), task not found (404), assignee not found (404).

- **Verify behavior đúng trọng tâm**
  - Có `verify(...)`, `never()`, `verifyNoInteractions(...)`.
  - Dùng `ArgumentCaptor<TaskEntity>` để assert mapping khi `taskRepository.save(...)`.

- **Readability/maintainability khá**
  - Tên test theo pattern `should_<expected>_when_<condition>`.
  - Có helper tạo dữ liệu test (factory methods) và hàm `assertApiException(...)` để tránh lặp.

### Điểm cần cải thiện / rủi ro

- **Mapping `deadline` trong `createTask` chưa được kiểm chứng**
  - `TaskEntity.deadline` là `@NotNull` nhưng `TaskService.createTask(...)` hiện không set `deadline` từ `TaskRequest`.
  - Unit test hiện tại chưa assert về `deadline`, nên có thể bỏ sót bug runtime (persist fail/constraint violation).

- **Test chưa cover edge case “assignee <= 0”**
  - Trong service có nhánh: `if (request.getAssignee() != null && request.getAssignee() > 0)`.
  - Nên bổ sung test cho `assignee = 0` hoặc `assignee = -1` để đảm bảo service bỏ qua lookup user.

- **Exception code cho invalid status đang là `404`**
  - Theo semantics thông thường nên là `400` (bad request). Đây là logic hiện tại của service (`parseStatus`), test đang bám đúng behavior.
  - Nếu tương lai muốn chuẩn hóa API contract, cần đổi service và cập nhật test.

- **`assignTask` giả định `taskEntity.getProject()` không null**
  - Hiện không có guard; test cũng không cover NPE case.
  - Có thể chấp nhận nếu DB constraint đảm bảo, nhưng nếu task có thể tồn tại sai dữ liệu thì nên thêm xử lý.

### Đề xuất action items

1. **(Ưu tiên cao)** Thêm test assert `deadline` được map đúng khi create task (expected fail trước khi sửa service).
2. Thêm test cho `assignee <= 0` để cover nhánh.
3. (Optional) Chuẩn hóa error code cho invalid status về `400` nếu API contract cần.

## 3) Review chất lượng `TaskRequestValidationTest`

### Điểm tốt

- Test chạy nhanh, độc lập Spring.
- Check được constraint cơ bản cho `title` và `deadline`.

### Điểm cần cải thiện

- Chưa cover các constraint khác:
  - `status` blank / quá dài
  - `project` null
  - `assignee` null

### Đề xuất

- Bổ sung test cho từng field constraint theo pattern AAA, giúp validation coverage sát với annotation ở `TaskRequest`.

## 4) Kết luận

- Bộ test hiện tại **đúng hướng** (unit test isolate tốt, verify interaction rõ, branch lỗi chính đã có).
- Điểm rủi ro lớn nhất là `deadline` trong `createTask` (entity bắt buộc, service chưa set) — nên fix sớm để tránh lỗi runtime.

---

Tài liệu này dùng để nộp/đính kèm vào report tuần: mô tả bộ test, điểm mạnh, điểm yếu và đề xuất cải thiện.

