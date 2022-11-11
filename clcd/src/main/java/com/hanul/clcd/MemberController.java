package com.hanul.clcd;

import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;

import common.CommonService;
import member.MemberServiceImpl;
import member.MemberVO;

@Controller
public class MemberController {
	
	@Autowired private MemberServiceImpl service;
	@Autowired private CommonService common;
	
	private String naver_client_id = "tHElZTrWaXlLGtVMbN4o";
	
	
	@RequestMapping ("/join")
	public String member_join(MemberVO vo) {
		service.member_join(vo);
		return "redirect:/";
	}
	
	//아이디 중복확인 요청
	@ResponseBody
	@RequestMapping ("/id_check")
	public boolean id_check(String id) {
		return service.member_id_check(id);
	}
	
	@RequestMapping ("/member")
	public String member(HttpSession session) {
		session.setAttribute("category", "join");
		return "member/join";
	}
	
	// 로그인 처리 요청
	@ResponseBody
	@RequestMapping ("/memberLogin")
	public boolean login(String id, String pw, HttpSession session) {
		// 화면에서 전송한 아이디, 비밀번호가 일치하는 회원정보를 DB에서 조회
		// 매개변수 2개를 HashMap 형태롤 담아 service에 전달
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("pw", pw);
		MemberVO vo = service.member_login(map);
		
		session.setAttribute("loginInfo", vo);
		
		return vo == null ? false : true;
	}
	
	// 로그인 화면 요청
	@RequestMapping ("/login")
	public String login(HttpSession session) {
		session.setAttribute("category", "login");
		return "member/login";
	}
	
	@RequestMapping ("/logout")
	public String logout(HttpSession session) {
		
		session.setAttribute("loginInfo", null);
		
		return "redirect:/";
	}
		
	@RequestMapping ("/naverLogin")
	public String naverlogin(HttpSession session) {
		// 3.4.2 네이버 로그인 연동 URL 생성하기
		
		String state = UUID.randomUUID().toString();
		
		// state 정보를 session 범위 내에서 계속 사용해야 하므로 session에 담음
		session.setAttribute("state", state);
		//https://nid.naver.com/oauth2.0/authorize?response_type=code
		//&client_id=CLIENT_ID
		//&state=STATE_STRING
		//&redirect_uri=CALLBACK_URL

		StringBuffer url = new StringBuffer("https://nid.naver.com/oauth2.0/authorize?response_type=code");
		url.append("&client_id=").append(naver_client_id);
		url.append("&state=").append(state);
		url.append("&&redirect_uri=http://localhost/clcd/navercallback");
		
		
		
		return "redirect:"+url.toString();
	}
	
	@RequestMapping ("/navercallback")
	public String navercallback(@RequestParam(required = false) String code, String state,
			@RequestParam(required = false) String error, HttpSession session) {
		// state 값이 맞지 않거나 error가 발생해도 토큰 발급 불가
		if( !state.equals(session.getAttribute("state")) || error != null ) {
			return "redirect:/"; // 메인 페이지로 이동
		}
		
		// 개발 가이드 3.4.4 접근 토큰 발급 요청 참조
		
		// 접근 토큰 발급을 위한 요청문 샘플을 복사하여 붙여넣기...
		
		//https://nid.naver.com/oauth2.0/token?grant_type=authorization_code
//		&client_id=jyvqXeaVOVmV
//		&client_secret=527300A0_COq1_XV33cf
//		&code=EIc5bFrl4RibFls1
//		&state=9kgsGTfH4j7IyAkg  
		
		StringBuffer url = new StringBuffer("https://nid.naver.com/oauth2.0/token?grant_type=authorization_code");
		url.append("&client_id=").append(naver_client_id);
		url.append("&client_secret=bq_6KN7HD0");
		url.append("&code=").append(code);
		url.append("&state=").append(state);
		
		JSONObject json = new JSONObject(common.requestAPI(url));
		String token = json.getString("access_token");
		String type = json.getString("token_type");
		
		
		url = new StringBuffer("https://openapi.naver.com/v1/nid/me");
		json = new JSONObject(common.requestAPI(url,type+" "+token));
		
		if(json.getString("resultcode").equals("00")) {
			json = json.getJSONObject("response");
			MemberVO vo = new MemberVO();
			
			vo.setSocial_type("naver");
			vo.setId(json.getString("id"));
			vo.setSocial_email(json.getString("email"));
			vo.setName(json.getString("name"));
			vo.setGender(json.has("gender") && json.getString("gender").equals("F") ? "여" : "남");
			// 네이버 최초 로그인인 경우 회원정보 저장 (insert)
			// 네이버 로그인 이력이 있어 회원정보가 있다면 변경 저장
			if (service.member_social_email(vo)) {
				service.member_social_update(vo);
			}else {
				service.member_social_insert(vo);
			}
			session.setAttribute("loginInfo", vo);
		}
		
		return "redirect:/";
	}
	
	
	
}
