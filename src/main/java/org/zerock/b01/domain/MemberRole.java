package org.zerock.b01.domain;

public enum MemberRole {
    // 이뮴 타입으로 생성 (사용자 권한에 따른 db 부여)
    USER,ADMIN;
    // USER = 0
    // ADMIN = 1
}
