/**	공통 스크립트: 입력항목 확인
 * 	입력한 내용이 없을 경우 저장 불가 처리*/
 
 function emptyCheck(){
	var ok = true;
	$('.chk').each( function() {
		if ( $(this).val() == '' ) {
			
			alert($(this).attr('title')+'을 입력하세요!');
			$(this).focus();
			ok = false;
			return ok;
		}
		
		
	});
	return ok;
}