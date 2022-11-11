<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h3>방명록 수정</h3>
<form action="update.bo" method="post" enctype="multipart/form-data">
<input type="hidden" name="attach"/>
<input type="hidden" name="id" value="${vo.id }"/>
<table>
	<tr>
		<th class='w-px120'>제목</th>
		<td>
			<input type="text" name="title" class="chk" title="제목" value="${vo.title }"/>
		</td>
	</tr>
	<tr>
		<th>내용</th>
		<td>
			<textarea name="content" class="chk" title="내용">${vo.content }</textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="left middle">
			<label>
				<input type="file" name="file" id="attach-file"/>
				<a><img src="imgs/attach.png" class="file-img"/></a>
			</label>
			<span id="file-name">${vo.filename }</span>
			<span id="preview"></span>
			<a id="delete-file"><i class="fas fa-times font-img"></i></a>
		</td>
	</tr>
</table>
</form>
<div class='btnSet'>
	<a class='btn-fill' 
	onclick=" if( emptyCheck()) { $('[name=attach]').val($('#file-name').text() );  $('form').submit() }">저장</a>
	<a class="btn-empty" onclick="history.go(-1)">취소</a>
</div>
<script type="text/javascript" src="js/file_check.js"></script>
<script type="text/javascript">
if(${! empty vo.filename}){
	$('#delete-file').css('display','inline');
	if(isImage('${vo.filename}')){
		$('#preview').html('<img src="${vo.filepath}" id="preview-img"/>' );
	}
}

</script>

</body>
</html>