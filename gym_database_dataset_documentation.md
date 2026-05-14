# Gym Appointment and Facility Management System 数据集说明文档

> SQL 文件：`数据集_updated.sql`  
> 数据库名称：`group2_gym_system`  
> 项目主题：Gym Appointment and Facility Management System  
> 版本说明：Updated MySQL Database Script

---

## 1. 数据集概述

这个 SQL 文件用于创建一个 **健身房预约与设施管理系统** 的 MySQL 数据库。系统主要支持以下功能：

1. 用户管理：区分 Student、Teacher、Admin 三种角色。
2. 场馆管理：管理 Fitness Area、Dance Room、Basketball Room、Swimming Pool、Badminton Room、PingPong Room 六类场馆。
3. 设备管理：记录不同场馆下设备的状态，例如 Working、Broken、Under Maintenance。
4. 预约管理：学生和老师可以预约场馆；管理员不能预约。
5. 维修管理：管理员可以记录设备维修信息。
6. 统计查询：通过 `AppointmentInfo` 视图查看预约、用户、场馆和设备状态的综合信息。

---

## 2. 数据库创建

```sql
CREATE DATABASE IF NOT EXISTS group2_gym_system;
USE group2_gym_system;
```

该语句会创建并使用名为 `group2_gym_system` 的数据库。

---

## 3. 主要实体表说明

### 3.1 User 表

`User` 表用于保存系统中的所有用户，包括学生、老师和管理员。

```sql
CREATE TABLE User (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    role ENUM('Student', 'Teacher', 'Admin') NOT NULL
);
```

| 字段名 | 类型 | 说明 |
|---|---|---|
| `user_id` | INT | 用户编号，主键，自增 |
| `name` | VARCHAR(100) | 用户姓名，不能为空 |
| `phone_number` | VARCHAR(20) | 电话号码，唯一，不能为空 |
| `role` | ENUM | 用户角色，只能是 Student、Teacher 或 Admin |

---

### 3.2 Student / Teacher / Admin 子类表

这三个表用于体现 User 的子类关系。

```sql
CREATE TABLE Student (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE
);
```

`Teacher` 和 `Admin` 表结构类似。

| 表名 | 作用 |
|---|---|
| `Student` | 保存学生用户 |
| `Teacher` | 保存老师用户 |
| `Admin` | 保存管理员用户 |

这些表的 `user_id` 同时是主键和外键，引用 `User(user_id)`。

如果 `User` 表中的某个用户被删除，对应的 Student、Teacher 或 Admin 记录也会自动删除，因为使用了：

```sql
ON DELETE CASCADE
```

---

### 3.3 Gym 表

`Gym` 表用于保存所有场馆的通用信息。

```sql
CREATE TABLE Gym (
    gym_id INT PRIMARY KEY AUTO_INCREMENT,
    gym_type ENUM(
        'Fitness Area',
        'Dance Room',
        'Basketball Room',
        'Swimming Pool',
        'Badminton Room',
        'PingPong Room'
    ) NOT NULL,
    location VARCHAR(200) NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    CHECK (close_time > open_time)
);
```

| 字段名 | 类型 | 说明 |
|---|---|---|
| `gym_id` | INT | 场馆编号，主键，自增 |
| `gym_type` | ENUM | 场馆类型 |
| `location` | VARCHAR(200) | 场馆位置 |
| `open_time` | TIME | 开放时间 |
| `close_time` | TIME | 关闭时间 |

重要约束：

```sql
CHECK (close_time > open_time)
```

这保证关闭时间必须晚于开放时间。

---

### 3.4 Equipment 表

`Equipment` 表用于保存设备信息。

```sql
CREATE TABLE Equipment (
    equipment_id VARCHAR(100) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    gym_id INT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE
);
```

| 字段名 | 类型 | 说明 |
|---|---|---|
| `equipment_id` | VARCHAR(100) | 设备编号，主键 |
| `status` | VARCHAR(50) | 设备状态 |
| `gym_id` | INT | 所属场馆编号，外键 |
| `last_updated` | TIMESTAMP | 最后更新时间 |

设备状态示例：

- `Working`
- `Broken`
- `Under Maintenance`

注意：这个版本的 SQL 文件中，`Equipment` 表 **没有 `equipment_name` 字段**。

---

### 3.5 Appointment 表

`Appointment` 表用于保存场馆预约信息。

```sql
CREATE TABLE Appointment (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    gym_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    payment DECIMAL(10,2) DEFAULT 0,
    record TEXT,
    FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE,
    FOREIGN KEY (gym_id) REFERENCES Gym(gym_id) ON DELETE CASCADE,
    CHECK (end_time > start_time),
    CHECK (payment >= 0)
);
```

| 字段名 | 类型 | 说明 |
|---|---|---|
| `appointment_id` | INT | 预约编号，主键，自增 |
| `user_id` | INT | 预约用户编号，外键 |
| `gym_id` | INT | 被预约场馆编号，外键 |
| `start_time` | DATETIME | 预约开始时间 |
| `end_time` | DATETIME | 预约结束时间 |
| `payment` | DECIMAL(10,2) | 预约费用 |
| `record` | TEXT | 预约备注 |

重要说明：

1. `Appointment` 表 **没有 `status` 字段**。
2. 单次预约时间不能超过 2 小时，这个规则由触发器控制。
3. 对于羽毛球、乒乓球、舞蹈室等需要预约具体场地/设备的情况，具体单位保存在已有的 `record` 字段中。

示例：

```text
Booked Unit: badminton_court1_equipment
Booked Unit: pingpong_table1_equipment
Booked Unit: dance_room1
```

---

### 3.6 Repair 表

`Repair` 表用于保存设备维修记录。

```sql
CREATE TABLE Repair (
    repair_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    equipment_id VARCHAR(100) NOT NULL,
    time_point DATETIME NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES Admin(user_id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id) ON DELETE CASCADE
);
```

| 字段名 | 类型 | 说明 |
|---|---|---|
| `repair_id` | INT | 维修记录编号，主键，自增 |
| `admin_id` | INT | 管理员编号，外键 |
| `equipment_id` | VARCHAR(100) | 被维修设备编号，外键 |
| `time_point` | DATETIME | 维修时间 |

---

## 4. 场馆子表说明

不同类型的场馆有不同的额外属性，所以 SQL 文件中为每种场馆建立了对应的子表。

| 表名 | 对应场馆类型 | 主要额外字段 |
|---|---|---|
| `Fitness_Area` | Fitness Area | `equipment_count`, `price` |
| `Dance_Room` | Dance Room | `time`, `price` |
| `Basketball_Room` | Basketball Room | `court_count`, `price` |
| `Swimming_Pool` | Swimming Pool | `price` |
| `Badminton_Room` | Badminton Room | `court_count`, `time`, `price` |
| `PingPong_Room` | PingPong Room | `table_count`, `time`, `price` |

这些子表都通过 `gym_id` 引用 `Gym(gym_id)`。

---

## 5. 表之间的关系

### 5.1 User 与 Student / Teacher / Admin

关系：

```text
User 1 : 1 Student
User 1 : 1 Teacher
User 1 : 1 Admin
```

含义：

- 所有用户都先存在于 `User` 表。
- 如果该用户是学生，就在 `Student` 表中也有一条记录。
- 如果该用户是老师，就在 `Teacher` 表中也有一条记录。
- 如果该用户是管理员，就在 `Admin` 表中也有一条记录。

---

### 5.2 Gym 与 Equipment

关系：

```text
Gym 1 : N Equipment
```

含义：一个场馆可以有多个设备，一个设备只属于一个场馆。

---

### 5.3 User 与 Appointment

关系：

```text
User 1 : N Appointment
```

含义：一个用户可以有多个预约，一个预约只属于一个用户。

---

### 5.4 Gym 与 Appointment

关系：

```text
Gym 1 : N Appointment
```

含义：一个场馆可以被预约多次，一个预约对应一个场馆。

---

### 5.5 Admin 与 Repair

关系：

```text
Admin 1 : N Repair
```

含义：一个管理员可以创建多条维修记录。

---

### 5.6 Equipment 与 Repair

关系：

```text
Equipment 1 : N Repair
```

含义：一个设备可以有多条维修记录。

---

## 6. 初始数据说明

### 6.1 用户数据

SQL 文件中插入了 8 个用户。

| user_id | name | role |
|---:|---|---|
| 1 | John Smith | Student |
| 2 | Emma Johnson | Student |
| 3 | Michael Brown | Student |
| 4 | Sarah Davis | Teacher |
| 5 | David Wilson | Teacher |
| 6 | Lisa Anderson | Admin |
| 7 | James Taylor | Student |
| 8 | Maria Garcia | Teacher |

---

### 6.2 场馆数据

SQL 文件中插入了 6 个场馆。

| gym_id | gym_type | location | open_time | close_time |
|---:|---|---|---|---|
| 1 | Fitness Area | Fitness Gym | 08:00:00 | 21:00:00 |
| 2 | Dance Room | Dance Room | 12:00:00 | 21:00:00 |
| 3 | Basketball Room | Basketball Court | 08:00:00 | 21:00:00 |
| 4 | Swimming Pool | Swimming Pool | 12:00:00 | 20:30:00 |
| 5 | Badminton Room | Badminton Court | 08:00:00 | 21:00:00 |
| 6 | PingPong Room | PingPong Room | 08:00:00 | 21:00:00 |

---

### 6.3 场馆价格数据

| 场馆类型 | 价格 |
|---|---:|
| Fitness Area | 10.00 |
| Dance Room | 100.00 |
| Basketball Room | 10.00 |
| Swimming Pool | 20.00 |
| Badminton Room | 15.00 |
| PingPong Room | 10.00 |

---

### 6.4 设备数据概览

SQL 文件中共插入了 22 条设备数据。

| 场馆 | 设备数量 | 状态说明 |
|---|---:|---|
| Fitness Area | 2 | 全部 Working |
| Basketball Room | 4 | 其中 basketball_court3_equipment 为 Broken |
| Badminton Room | 6 | court3 和 court6 为 Under Maintenance |
| PingPong Room | 10 | table9 为 Broken |

---

### 6.5 预约数据概览

SQL 文件中插入了 8 条预约数据。

| appointment_id | user_id | gym_id | start_time | end_time | payment | record |
|---:|---:|---:|---|---|---:|---|
| 1 | 1 | 1 | 2024-01-15 10:00:00 | 2024-01-15 12:00:00 | 20.00 | Fitness booking by student |
| 2 | 7 | 1 | 2024-01-21 09:00:00 | 2024-01-21 11:00:00 | 20.00 | Fitness booking by student |
| 3 | 2 | 2 | 2024-01-16 14:00:00 | 2024-01-16 16:00:00 | 200.00 | Booked Unit: dance_room1 |
| 4 | 3 | 3 | 2024-01-17 16:00:00 | 2024-01-17 18:00:00 | 20.00 | Basketball booking |
| 5 | 8 | 3 | 2024-01-22 19:00:00 | 2024-01-22 21:00:00 | 20.00 | Basketball booking by teacher |
| 6 | 4 | 4 | 2024-01-18 18:00:00 | 2024-01-18 20:00:00 | 40.00 | Swimming booking by teacher |
| 7 | 5 | 5 | 2024-01-19 18:00:00 | 2024-01-19 20:00:00 | 30.00 | Booked Unit: badminton_court1_equipment |
| 8 | 1 | 6 | 2024-01-20 15:00:00 | 2024-01-20 17:00:00 | 20.00 | Booked Unit: pingpong_table1_equipment |

---

### 6.6 维修数据

SQL 文件中插入了 3 条维修记录。

| repair_id | admin_id | equipment_id | time_point |
|---:|---:|---|---|
| 1 | 6 | badminton_court3_equipment | 2024-01-10 09:00:00 |
| 2 | 6 | pingpong_table9_equipment | 2024-01-11 14:00:00 |
| 3 | 6 | badminton_court3_equipment | 2024-01-12 16:00:00 |

---

## 7. 触发器说明

SQL 文件中创建了多个触发器，用于保证业务逻辑正确。

### 7.1 用户角色检查触发器

| 触发器名 | 作用 |
|---|---|
| `check_user_role_student` | 插入 Student 前，检查 User.role 是否为 Student |
| `check_user_role_teacher` | 插入 Teacher 前，检查 User.role 是否为 Teacher |
| `check_user_role_admin` | 插入 Admin 前，检查 User.role 是否为 Admin |

例如，如果一个用户的 `role` 是 `Teacher`，却被插入 `Student` 表，数据库会报错。

---

### 7.2 阻止管理员预约

```sql
CREATE TRIGGER prevent_admin_appointment
BEFORE INSERT ON Appointment
FOR EACH ROW
BEGIN
    IF (SELECT role FROM User WHERE user_id = NEW.user_id) = 'Admin' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Admin cannot make appointments';
    END IF;
END
```

作用：管理员不能创建预约。

---

### 7.3 单次预约时间限制

| 触发器名 | 作用 |
|---|---|
| `limit_single_appointment_time` | 插入预约前检查预约时长 |
| `limit_single_appointment_time_update` | 更新预约前检查预约时长 |

规则：

```text
单次预约不能超过 120 分钟，也就是 2 小时。
```

如果超过 2 小时，会报错：

```text
Each appointment can only be up to 2 hours
```

---

### 7.4 场馆类型检查触发器

| 触发器名 | 作用 |
|---|---|
| `check_fitness_area_type` | 保证 Fitness_Area 表中的 gym_id 对应 Fitness Area |
| `check_dance_room_type` | 保证 Dance_Room 表中的 gym_id 对应 Dance Room |
| `check_basketball_room_type` | 保证 Basketball_Room 表中的 gym_id 对应 Basketball Room |
| `check_swimming_pool_type` | 保证 Swimming_Pool 表中的 gym_id 对应 Swimming Pool |
| `check_badminton_room_type` | 保证 Badminton_Room 表中的 gym_id 对应 Badminton Room |
| `check_pingpong_room_type` | 保证 PingPong_Room 表中的 gym_id 对应 PingPong Room |

这些触发器防止把错误类型的 `gym_id` 插入到错误的子表中。

---

## 8. 视图 AppointmentInfo 说明

SQL 文件创建了一个视图：

```sql
CREATE VIEW AppointmentInfo AS ...
```

这个视图把以下信息整合到一起：

1. 预约信息
2. 用户信息
3. 场馆信息
4. 预约时长
5. 每个场馆的预约数量
6. 每个场馆的设备数量
7. Working / Broken / Under Maintenance 设备数量

视图中的主要字段：

| 字段名 | 说明 |
|---|---|
| `appointment_id` | 预约编号 |
| `user_name` | 用户姓名 |
| `role` | 用户角色 |
| `phone_number` | 电话号码 |
| `gym_type` | 场馆类型 |
| `location` | 场馆位置 |
| `start_time` | 预约开始时间 |
| `end_time` | 预约结束时间 |
| `duration_hours` | 预约时长，单位为小时 |
| `payment` | 支付金额 |
| `record` | 预约备注 |
| `appointment_count_for_gym` | 该场馆的预约总数 |
| `total_equipment` | 该场馆设备总数 |
| `working_equipment` | 正常设备数量 |
| `broken_equipment` | 损坏设备数量 |
| `maintenance_equipment` | 维修中设备数量 |

这个视图适合用于 GUI 页面展示预约信息和场馆设备状态。

---

## 9. 索引说明

SQL 文件创建了多个索引来提高查询效率。

| 索引名 | 表 | 字段 | 作用 |
|---|---|---|---|
| `idx_appointment_user_id` | Appointment | `user_id` | 加快按用户查预约 |
| `idx_appointment_gym_id` | Appointment | `gym_id` | 加快按场馆查预约 |
| `idx_appointment_start_time` | Appointment | `start_time` | 加快按开始时间查预约 |
| `idx_appointment_gym_time` | Appointment | `gym_id, start_time, end_time` | 加快检查某场馆某时间段预约 |
| `idx_appointment_record_prefix` | Appointment | `record(100)` | 加快根据 record 中的具体场地/设备查询预约 |
| `idx_equipment_gym_id` | Equipment | `gym_id` | 加快按场馆查设备 |
| `idx_equipment_status` | Equipment | `status` | 加快按设备状态查询 |

---

## 10. 核心业务规则总结

| 规则 | 实现方式 |
|---|---|
| 用户电话号码不能重复 | `phone_number UNIQUE` |
| 用户只能是 Student、Teacher 或 Admin | `role ENUM(...)` |
| 关闭时间必须晚于开放时间 | `CHECK (close_time > open_time)` |
| 预约结束时间必须晚于开始时间 | `CHECK (end_time > start_time)` |
| 支付金额不能为负数 | `CHECK (payment >= 0)` |
| 管理员不能预约 | `prevent_admin_appointment` 触发器 |
| 单次预约不能超过 2 小时 | `limit_single_appointment_time` 触发器 |
| Student 表只能插入学生用户 | `check_user_role_student` 触发器 |
| Teacher 表只能插入老师用户 | `check_user_role_teacher` 触发器 |
| Admin 表只能插入管理员用户 | `check_user_role_admin` 触发器 |
| 场馆子表必须对应正确的 gym_type | 多个 `check_xxx_type` 触发器 |

---

## 11. 常用查询示例

### 11.1 查询所有预约信息

```sql
SELECT * FROM Appointment;
```

---

### 11.2 查询所有设备状态

```sql
SELECT equipment_id, status, gym_id
FROM Equipment;
```

---

### 11.3 查询损坏设备

```sql
SELECT equipment_id, gym_id
FROM Equipment
WHERE status = 'Broken';
```

---

### 11.4 查询某个用户的所有预约

```sql
SELECT
    u.name,
    g.gym_type,
    a.start_time,
    a.end_time,
    a.payment,
    a.record
FROM Appointment a
JOIN User u ON a.user_id = u.user_id
JOIN Gym g ON a.gym_id = g.gym_id
WHERE u.user_id = 1;
```

---

### 11.5 查询每个场馆的预约数量

```sql
SELECT
    g.gym_type,
    COUNT(a.appointment_id) AS appointment_count
FROM Gym g
LEFT JOIN Appointment a ON g.gym_id = a.gym_id
GROUP BY g.gym_id, g.gym_type;
```

---

### 11.6 查询每个场馆的设备状态统计

```sql
SELECT
    g.gym_type,
    COUNT(e.equipment_id) AS total_equipment,
    SUM(CASE WHEN e.status = 'Working' THEN 1 ELSE 0 END) AS working_equipment,
    SUM(CASE WHEN e.status = 'Broken' THEN 1 ELSE 0 END) AS broken_equipment,
    SUM(CASE WHEN e.status = 'Under Maintenance' THEN 1 ELSE 0 END) AS maintenance_equipment
FROM Gym g
LEFT JOIN Equipment e ON g.gym_id = e.gym_id
GROUP BY g.gym_id, g.gym_type;
```

---

### 11.7 使用 AppointmentInfo 视图查询综合信息

```sql
SELECT * FROM AppointmentInfo;
```

---

## 12. 如何导入这个 SQL 文件

### 方法一：MySQL Workbench

1. 打开 MySQL Workbench。
2. 连接到你的 MySQL Server。
3. 点击菜单栏：`File` → `Open SQL Script`。
4. 选择 `数据集_updated.sql`。
5. 点击闪电按钮运行全部 SQL。
6. 如果执行成功，会看到：

```text
Database setup complete!
```

---

### 方法二：命令行导入

```bash
mysql -u root -p < 数据集_updated.sql
```

如果你的用户名不是 `root`，请替换成自己的 MySQL 用户名。

---

## 13. 这个数据集适合展示的功能

这个数据库适合用于以下课程项目功能展示：

1. 登录后根据用户角色显示不同功能。
2. 学生和老师可以预约场馆。
3. 管理员不能预约，但可以管理维修记录。
4. 展示不同场馆的开放时间、价格和设备数量。
5. 展示设备状态，例如 Working、Broken、Under Maintenance。
6. 根据预约记录计算某个场馆的预约数量。
7. 使用视图 `AppointmentInfo` 简化 GUI 页面查询。
8. 使用触发器展示数据库层面的业务规则保护。

---

## 14. ER 图对应关系总结

| ER 概念 | SQL 实现 |
|---|---|
| User 实体 | `User` 表 |
| Student 子类 | `Student` 表 |
| Teacher 子类 | `Teacher` 表 |
| Admin 子类 | `Admin` 表 |
| Gym 实体 | `Gym` 表 |
| Equipment 实体 | `Equipment` 表 |
| Appointment 实体 | `Appointment` 表 |
| Repair 实体 | `Repair` 表 |
| 不同类型场馆 | `Fitness_Area`, `Dance_Room`, `Basketball_Room`, `Swimming_Pool`, `Badminton_Room`, `PingPong_Room` |
| 派生属性：场馆预约数量 | `AppointmentInfo` 视图中的 `appointment_count_for_gym` |
| 设备状态统计 | `AppointmentInfo` 视图中的 equipment summary 字段 |

---

## 15. 注意事项

1. 当前 `Appointment` 表没有 `status` 字段，这是为了符合 ER 图设计。
2. 具体预约的场地或设备没有新增字段，而是写在 `record` 字段中。
3. `Equipment` 表的主键是字符串类型的 `equipment_id`。
4. `Repair` 表也使用字符串类型的 `equipment_id` 作为外键。
5. 单次预约不能超过 2 小时，但当前触发器只限制单次预约时长，不限制同一用户同一天在同一场馆的累计预约时长。
6. 如果需要限制“同一用户同一天同一场馆累计预约不能超过 2 小时”，需要额外修改预约相关触发器或在 Java 代码中进行业务逻辑检查。

---

## 16. 一句话总结

这个 SQL 文件创建了一个完整的健身房预约与设施管理数据库，包含用户、场馆、设备、预约、维修、视图、索引和触发器，适合用于 Java GUI + MySQL 的课程项目展示。
