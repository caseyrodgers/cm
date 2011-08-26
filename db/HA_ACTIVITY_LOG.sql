create table HA_ACTIVITY_TIME (
type  varchar(50) primary key,
description varchar(100),
task_time int not null
);

insert into HA_ACTIVITY_TIME (type, description, task_time) values ('ACTIVITY', 'Required Practice Activity - Flash', 10);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('ACTIVITY_STANDARD', 'Game', 10);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('CMEXTRA', 'Extra practice problem', 5);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('EPP_WB', 'Extra practice problem whiteboard', 5);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('FLASHCARD', 'Required Practice Activity of Flashcard', 10);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('FLASHCARD_SPANISH', 'Required Practice Activity of Flashcard in Spanish', 10);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('PRACTICE', 'Required practice problem', 5);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('QUIZ', 'Quiz', 15);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('RESULTS', 'Quiz results', 3);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('REVIEW', 'Review Lesson', 3);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('RPP_WB', 'Required practice problem whiteboard', 5);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('VIDEO', 'Video', 5);
insert into HA_ACTIVITY_TIME (type, description, task_time) values ('WHITEBOARD', 'Whiteboard Activity', 5);