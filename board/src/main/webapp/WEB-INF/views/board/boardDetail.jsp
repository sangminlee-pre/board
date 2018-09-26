<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>   

<%		
	//JSP 캐시 삭제
	response.setHeader("Cache-Control","no-store");   
	response.setHeader("Pragma","no-cache");   
	response.setDateHeader("Expires",0);   
	
	if (request.getProtocol().equals("HTTP/1.1")) 
	    response.setHeader("Cache-Control", "no-cache"); 
	    
	// Context Root
	String contextRoot = response.encodeURL(request.getContextPath());
	String boardSeq = request.getParameter("boardSeq");		
%>

<c:set var="contextRoot" value="<%=contextRoot%>"/> <!-- Context Root -->
<c:set var="boardSeq" value="<%=boardSeq%>"/> <!-- 게시글 번호 -->

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>게시판 상세</title>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
	
	/** 전역변수 선언 */
	var _CONTEXTROOT = "${contextRoot}";
	var _URLDOMAIN 	= getUrlDomain();

	$(document).ready(function(){		
		
		var boardSeq = $("#board_seq").val();

		getBoardDetail(boardSeq);		
	});

	/** 도메인 값 얻기  */
	function getUrlDomain() {		
		return (location.href).replace("http://", "").replace("https://", "").split("/")[0];
	}
	
	/** 게시판 - 목록 페이지 이동 */
	function goBoardList(){				
		location.href = "http://" + _CONTEXTROOT + _URLDOMAIN + "/board/boardList";
	}
	
	/** 게시판 - 수정 페이지 이동 */
	function goBoardUpdate(){
		
		var boardSeq = $("#board_seq").val();
		
		location.href = "http://" + _CONTEXTROOT + _URLDOMAIN + "/board/boardUpdate?boardSeq="+ boardSeq;
	}
	
	/** 게시판 - 상세 조회  */
	function getBoardDetail(boardSeq){
		
		$.ajax({	
		
		    url		: "/board/getBoardDetail",
		    data    : $("#boardForm").serialize(),
	        dataType: "JSON",
	        cache   : false,
			async   : true,
			type	: "GET",	
	        success : function(obj) {
	        	getBoardDetailCallback(obj);				
	        },	       
	        error 	: function(xhr, status, error) {}
	        
	     });
	}
	
	/** 게시판 - 상세 조회  콜백 함수 */
	function getBoardDetailCallback(obj){
		
		var str = "";
		
		if(obj != null){								
							
			var boardSeq		= obj.board_seq; 
			var boardReRef 		= obj.board_re_ref; 
			var boardReLev 		= obj.board_re_lev; 
			var boardReSeq 		= obj.board_re_seq; 
			var boardWriter 	= obj.board_writer; 
			var boardSubject 	= obj.board_subject; 
			var boardContent 	= obj.board_content; 
			var boardHits 		= obj.board_hits;
			var delYn 			= obj.del_yn; 
			var insUserId 		= obj.ins_user_id;
			var insDate 		= obj.ins_date; 
			var updUserId 		= obj.upd_user_id;
			var updDate 		= obj.upd_date;
					
			str += "<tr>";
			str += "<th>제목</th>";
			str += "<td>"+ boardSubject +"</td>";
			str += "<th>조회수</th>";
			str += "<td>"+ boardHits +"</td>";
			str += "</tr>";		
			str += "<tr>";
			str += "<th>작성자</th>";
			str += "<td>"+ boardWriter +"</td>";
			str += "<th>작성일시</th>";
			str += "<td>"+ insDate +"</td>";
			str += "</tr>";		
			str += "<tr>";
			str += "<th>내용</th>";
			str += "<td colspan='3'>"+ boardSubject +"</td>";
			str += "</tr>";
			
		} else {
			
			alert("등록된 글이 존재하지 않습니다.");
			return;
		}		
		
		$("#tbody").html(str);
	}
	
	/** 게시판 - 삭제  */
	function deleteBoard(){

		var boardSeq = $("#board_seq").val();
		
		var yn = confirm("게시글을 삭제하시겠습니까?");		
		if(yn){
			
			$.ajax({	
				
			    url		: "/board/deleteBoard",
			    data    : $("#boardForm").serialize(),
		        dataType: "JSON",
		        cache   : false,
				async   : true,
				type	: "GET",	
		        success : function(obj) {
		        	deleteBoardCallback(obj);				
		        },	       
		        error 	: function(xhr, status, error) {}
		        
		     });
		}		
	}
	
	/** 게시판 - 삭제 콜백 함수 */
	function deleteBoardCallback(obj){
	
		if(obj != null){		
			
			var result = obj.result;
			
			if(result == "SUCCESS"){				
				alert("게시글 삭제를 성공하였습니다.");				
				goBoardList();				
			} else {				
				alert("게시글 삭제를 실패하였습니다.");	
				return;
			}
		}
	}
	
</script>
</head>
<body>
<h2>게시글 상세</h2>
<form id="boardForm" name="boardForm">	
	<table border=1 width="650px">
	    <colgroup>
	        <col width="15%">
	        <col width="35%">
	        <col width="15%">
	        <col width="*">
	    </colgroup>
	    <tbody id="tbody">
	       
	    </tbody>
	</table>	
	<input type="hidden" id="board_seq" name="board_seq" value="${boardSeq}"/> <!-- 게시글 번호 -->
</form>
<button onclick="javascript:goBoardList();">목록으로</button>
<button onclick="javascript:goBoardUpdate();">수정하기</button>
<button onclick="javascript:deleteBoard();">삭제하기</button>
</body>
</html>