-- ============================================================
-- DevSolve Database Schema (dbdiagram.io export format)
-- 멘토-멘티 매칭 과외 플랫폼
-- ============================================================

CREATE TABLE `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'MENTEE' COMMENT 'MENTOR, MENTEE, ADMIN',
  `introduction` TEXT,
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `refresh_token_storage` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `refresh_token` VARCHAR(500),
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `skill_name` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `mentors` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `career` TEXT COMMENT '경력 사항',
  `status` VARCHAR(20) NOT NULL DEFAULT 'APPROVED' COMMENT 'APPROVED',
  `review_count` INT NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `mentor_availability` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `day_of_week` VARCHAR(10) NOT NULL COMMENT 'MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY',
  `start_time` TIME NOT NULL COMMENT '시작 시간',
  `end_time` TIME NOT NULL COMMENT '종료 시간',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `mentor_skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `tutorials` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `price` INT NOT NULL COMMENT '1회 수업 가격',
  `duration` INT NOT NULL COMMENT '수업 시간(분)',
  `rating` DECIMAL(3,2) NOT NULL DEFAULT 0 COMMENT '평균 평점',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, PENDING, DELETED',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `tutorial_skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `payments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `imp_uid` VARCHAR(100) COMMENT 'PortOne 결제 고유 ID',
  `merchant_uid` VARCHAR(100) COMMENT '가맹점 주문 ID',
  `amount` INT NOT NULL COMMENT '결제 금액',
  `count` INT NOT NULL COMMENT '구매 횟수 (이용권 개수)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PAID, CANCELLED, REFUNDED',
  `paid_at` TIMESTAMP COMMENT '결제 완료 시간',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `tickets` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `payment_id` BIGINT NOT NULL,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `total_count` INT NOT NULL COMMENT '총 구매 횟수',
  `status` VARCHAR(20) COMMENT 'PENDING, ACTIVE, EXPIRED, USED, CANCELLED',
  `remaining_count` INT NOT NULL COMMENT '남은 횟수',
  `expired_at` TIMESTAMP COMMENT '유효기간 (기본: 구매일 +6개월)',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `lessons` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'REQUESTED' COMMENT 'REQUESTED, CONFIRMED, REJECTED, SCHEDULED, COMPLETED, CANCELLED',
  `request_message` TEXT COMMENT '수업 신청 메시지',
  `reject_reason` TEXT COMMENT '거절 사유',
  `scheduled_at` TIMESTAMP NOT NULL COMMENT '수업 예정 시간',
  `completed_at` TIMESTAMP COMMENT '수업 완료 시간',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `reviews` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `mentor_id` BIGINT NOT NULL,
  `rating` INT NOT NULL COMMENT '평점 (1-5)',
  `content` TEXT NOT NULL COMMENT '리뷰 내용',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `questions` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `lesson_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL COMMENT '질문 제목',
  `content` TEXT NOT NULL COMMENT '질문 내용',
  `code_content` TEXT COMMENT '코드 스니펫',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

CREATE TABLE `answers` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `question_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL COMMENT '답변 내용',
  `created_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
  `updated_at` TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP)
);

-- ============================================================
-- INDEXES
-- ============================================================

CREATE UNIQUE INDEX `uk_users_email` ON `users` (`email`);
CREATE UNIQUE INDEX `uk_users_nickname` ON `users` (`nickname`);
CREATE UNIQUE INDEX `uk_refresh_user` ON `refresh_token_storage` (`user_id`);
CREATE UNIQUE INDEX `uk_skill_name` ON `skills` (`skill_name`);
CREATE UNIQUE INDEX `uk_mentor_user` ON `mentors` (`user_id`);
CREATE UNIQUE INDEX `uk_mentor_day` ON `mentor_availability` (`mentor_id`, `day_of_week`);
CREATE INDEX `idx_availability_mentor` ON `mentor_availability` (`mentor_id`);
CREATE UNIQUE INDEX `uk_mentor_skill` ON `mentor_skills` (`mentor_id`, `skill_id`);
CREATE INDEX `idx_mentor_skills_mentor` ON `mentor_skills` (`mentor_id`);
CREATE INDEX `idx_mentor_skills_skill` ON `mentor_skills` (`skill_id`);
CREATE INDEX `idx_tutorials_mentor` ON `tutorials` (`mentor_id`);
CREATE INDEX `idx_tutorials_status` ON `tutorials` (`status`);
CREATE UNIQUE INDEX `uk_tutorial_skill` ON `tutorial_skills` (`tutorial_id`, `skill_id`);
CREATE INDEX `idx_tutorial_skills_tutorial` ON `tutorial_skills` (`tutorial_id`);
CREATE INDEX `idx_tutorial_skills_skill` ON `tutorial_skills` (`skill_id`);
CREATE UNIQUE INDEX `uk_merchant_uid` ON `payments` (`merchant_uid`);
CREATE INDEX `idx_payments_tutorial` ON `payments` (`tutorial_id`);
CREATE INDEX `idx_payments_mentee` ON `payments` (`mentee_id`);
CREATE INDEX `idx_payments_status` ON `payments` (`status`);
CREATE INDEX `idx_tickets_payment` ON `tickets` (`payment_id`);
CREATE INDEX `idx_tickets_tutorial` ON `tickets` (`tutorial_id`);
CREATE INDEX `idx_tickets_mentee` ON `tickets` (`mentee_id`);
CREATE INDEX `idx_tickets_expired` ON `tickets` (`expired_at`);
CREATE INDEX `idx_lessons_ticket` ON `lessons` (`ticket_id`);
CREATE INDEX `idx_lessons_status` ON `lessons` (`status`);
CREATE INDEX `idx_lessons_scheduled` ON `lessons` (`scheduled_at`);
CREATE UNIQUE INDEX `uk_review_mentee_tutorial` ON `reviews` (`mentee_id`, `tutorial_id`);
CREATE INDEX `idx_reviews_tutorial` ON `reviews` (`tutorial_id`);
CREATE INDEX `idx_reviews_mentee` ON `reviews` (`mentee_id`);
CREATE INDEX `idx_reviews_mentor` ON `reviews` (`mentor_id`);
CREATE INDEX `idx_questions_lesson` ON `questions` (`lesson_id`);
CREATE INDEX `idx_answers_question` ON `answers` (`question_id`);

-- ============================================================
-- FOREIGN KEYS
-- ============================================================

ALTER TABLE `refresh_token_storage` ADD CONSTRAINT `fk_refresh_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
ALTER TABLE `mentors` ADD CONSTRAINT `fk_mentor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
ALTER TABLE `mentor_availability` ADD CONSTRAINT `fk_availability_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE;
ALTER TABLE `mentor_skills` ADD CONSTRAINT `fk_mentorskill_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE;
ALTER TABLE `mentor_skills` ADD CONSTRAINT `fk_mentorskill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE;
ALTER TABLE `tutorials` ADD CONSTRAINT `fk_tutorial_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE;
ALTER TABLE `tutorial_skills` ADD CONSTRAINT `fk_tutorialskill_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`) ON DELETE CASCADE;
ALTER TABLE `tutorial_skills` ADD CONSTRAINT `fk_tutorialskill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE;
ALTER TABLE `payments` ADD CONSTRAINT `fk_payment_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`);
ALTER TABLE `payments` ADD CONSTRAINT `fk_payment_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`);
ALTER TABLE `tickets` ADD CONSTRAINT `fk_ticket_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`);
ALTER TABLE `tickets` ADD CONSTRAINT `fk_ticket_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`);
ALTER TABLE `tickets` ADD CONSTRAINT `fk_ticket_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`);
ALTER TABLE `lessons` ADD CONSTRAINT `fk_lesson_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`);
ALTER TABLE `reviews` ADD CONSTRAINT `fk_review_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`) ON DELETE CASCADE;
ALTER TABLE `reviews` ADD CONSTRAINT `fk_review_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`);
ALTER TABLE `reviews` ADD CONSTRAINT `fk_review_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`);
ALTER TABLE `questions` ADD CONSTRAINT `fk_question_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`id`) ON DELETE CASCADE;
ALTER TABLE `answers` ADD CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE;
