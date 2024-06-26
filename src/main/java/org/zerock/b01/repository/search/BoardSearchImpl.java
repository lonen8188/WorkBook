package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.QBoard;

import java.util.List;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl(){
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {

        QBoard board = QBoard.board;  // Q도메인 객체 사용

        JPQLQuery<Board> query = from(board); // select ??? from board ~~~~
        // JPQL : 엔티티 객체를 대상으로 쿼리함
        // from : 쿼리 소스를 추가한다.
        // innerJoin, join, leftJoin, fullJoin, on : 조인 부분을 추가한다. 조인 메서드에서 첫 번째 인자는 조인 소스이고, 두 번째 인자는 대상(별칭)이다.
        // where : 쿼리 필터를 추가한다. 가변 인자나 AND/OR 메서드를 이용해 필터를 추가한다.
        // groupBy : 가변인자 형식의 인자를 기준으로 그룹을 추가한다.
        // having : Predicate 표현식을 이용해 "group by" 그룹핑의 필터를 추가한다.
        // orderBy : 정렬 표현식을 이용해서 정렬 순서를 지정한다. 숫자나 문자열에 대해서는 asc()나 desc()를 사용하고, OrderSpecifier에 접근하기 위해 다른 비교 표현식을 사용한다.
        // limit, offset, restrict : 결과의 페이징을 설정한다. limit은 최대 결과 개수, offset은 결과의 시작 행, restrict는 limit과 offset을 함께 정의한다

        BooleanBuilder booleanBuilder = new BooleanBuilder(); // ( 객체 생성

        booleanBuilder.or(board.title.contains("11")); // 조건 1 title like ...

        booleanBuilder.or(board.content.contains("11")); // 조건 2 content like ....

        query.where(booleanBuilder);  // where 조건문 추가
        query.where(board.bno.gt(0L)); // 인덱싱을 사용하기 위해 bno > 0 로 검색 조건 추가


        //paging Querydsl().applyPagination기능은 실행 마지막 쿼리에 limit가 적용
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();
        //fetch() :  리스트로 결과를 반환하는 방법입니다. (만약에 데이터가 없으면 빈 리스트를 반환해줍니다.)
        //fetchOne() : 단건을 조회할 때 사용하는 방법인데요. (결과가 없을때는 null 을 반환하고 결과가 둘 이상일 경우에는 NonUniqueResultException을 던집니다.)
        //fetchFirst() : 처음의 한건을 쿼리해서 가져오고 싶을때 사용하고요.
        //fetchResults() : 해당 내용은 페이징을 위해 사용될 수 있습니다. 페이징을 위해서 total contents를 가져오고요.
        //fetchCount() : count 쿼리를 날릴 수 있다.

        long count = query.fetchCount();


        return null;

    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        // String[] types (제목t, 내용c, 작성자w)를 가지고 있는 문자열배열

        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        if( (types != null && types.length > 0) && keyword != null ){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }//end if

        //bno > 0
        query.where(board.bno.gt(0L));

        //paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);
        // 데이터를 가져온뒤 List 를 PageImpl 로 변환하기

    }


}
