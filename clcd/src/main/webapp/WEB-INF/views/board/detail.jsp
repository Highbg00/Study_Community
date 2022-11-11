<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
#popup {
	width: 450px;
	height: 450px;
	border: 2px solid #666;
	display: none;
}
.popup{ width:100%; height: 100%;}

#popup-background {
	position: absolute; left: 0; top: 0; width: 100%; height: 100%;
	background-color: #000; opacity: 0.3; display: none;
}
.comment {
	margin: 0 auto; padding-top: 20px; width: 500px;
}
textarea#comment { width: 95%; height: 60px; margin-top: 5px; resize: none;}
#comment_regist {width: 100%;}
#comment_regist span {width: 50%; float: left;}
</style>
</head>
<body>
<h3>방명록 안내</h3>
<table>
	<tr>
		<th class='w-px120'>제목</th>
		<td colspan="5" class="left">${vo.title }</td>
	</tr>
	<tr>
		<th>작성자</th>
		<td>${vo.name }</td>
		<th class='w-px120'>작성일자</th>
		<td class='w-px120'>${vo.writedate }</td>
		<th class='w-px80'>조회수</th>
		<td class='w-px80'>${vo.readcnt }</td>
	</tr>
	<tr>
		<th>내용</th>
		<td colspan="5" class="left">${fn:replace(vo.content,crlf, '<br>') }</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td colspan="5" class='left'>${vo.filename }
			<c:if test="${ ! empty vo.filename }">
				<a id='preview'></a>
				<a href='download.bo?id=${vo.id }'><i class="fa-solid fa-download"></i></a>			
			</c:if>		
		</td>
	</tr>
	<tr>
		<td colspan="6">
		<c:if test="${vo.prev ne 0 }">
			<a href='detail.bo?id=${vo.prev }'>[이전 글] ${vo.prev_title }</a>
		</c:if>
		<c:if test="${vo.next ne 0 }">
			<a href='detail.bo?id=${vo.next }'>[다음 글] ${vo.next_title }</a>
		</c:if>
		</td>
	</tr>
</table>
<div class='btnSet'>
	<a class='btn-fill' onclick="$('form').submit()">목록으로</a>
	
	<c:if test="${loginInfo.id eq vo.writer }">
		<%-- 		<a class='btn-fill' href='modify.bo?id=${vo.id }'>수정</a> --%>
		<a class='btn-fill' onclick="$('form').attr('action', 'modify.bo'); $('form').submit()">수정</a>
		<a class='btn-fill' onclick="if (confirm ('정말 삭제하시겠습니까?')) { href='delete.bo?id=${vo.id}' } ">삭제</a>
	</c:if>
</div>

<div class='comment'>
	<div id='comment_regist'>
		<span class='left'>댓글작성</span>
		<span class='right'><a class='btn-fill-s' onclick="comment_regist()">댓글등록</a></span>
		<textarea id='comment'></textarea>
	</div>
	<div id='comment_list'></div>
</div>

<form action="list.bo" method="post">
	<input type="hidden" name="id" value="${vo.id  }"/>
	<input type="hidden" name ='search' value="${page.search }"/>
	<input type="hidden" name ="keyword" value="${page.keyword }"/>
	<input type="hidden" name="pageList" value="${page.pageList }"/>
	<input type="hidden" name="curPage" value="${page.curPage }"/>
	<input type="hidden" name="viewType" value="${page.viewType }"/>
</form>

</body>

<div id ='popup-background'></div>
<div id = 'popup' class='center'></div>

<script type="text/javascript" src="js/file_check.js?v<%=new Date().getTime() %>"></script>
<script type="text/javascript">
$(function(){
	if ( ${ ! empty vo.filename }) {	// 첨부 파일이 있을 경우
		if ( isImage('${vo.filename}') ) {	// 첨부파일이 이미지인 경우 
			$('#preview').html("<img src='${vo.filepath}' id='preview-img' />");
		}
	}
});
		
		
$(document).on('click', '#preview-img', function () {
	$('#popup, #popup-background').css('display', 'block');
	$('#popup').html("<img src='${vo.filepath}' class='popup' />");
}).on('click', '#popup-background', function () {
	$('#popup, #popup-background').css('display', 'none');
});

</script>

<script type="text/javascript">
function comment_regist(){
	if(${empty loginInfo}) {
		alert("댓글을 등록하려면 로그인하세요!")
		return;
	}else if($.trim($('#comment').val()) == ''){
		alert('댓글을 입력하세요!');
		$('#comment').val('');
		$('#comment').focus();
		
		return;
	}
	
 	$.ajax({
		url: 'board/comment/regist'
		, data: { pid : ${vo.id}, content:$('#comment').val()}
		, success: function(response){
			if(response){
				alert('댓글이 등록되었습니다.');
				$('#comment').val('');
				comment_list();
			}else{
				alert('댓글 등록이 실패되었습니다.')
			}
		}, error : function(req, text){
			alert(text+req.status);
		}
	}); 
}

function comment_list(){
	$.ajax({
		url : 'board/comment/list/${vo.id}'
		//, data : {pid : 772}
		, success : function(response){
			$('#comment_list').html(response);
		}, error : function(req, text){
			alert(text+':'+req.status);
		}
	});
}

$(function (){
	comment_list();
});
</script>
</html>