package com.hanul.clcd;

import java.io.File;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import common.CommonService;
import member.MemberServiceImpl;
import member.MemberVO;
import notice.NoticePage;
import notice.NoticeServiceImpl;
import notice.NoticeVO;

@Controller
public class NoticeController {
	@Autowired private NoticeServiceImpl service;

	// 회원 정보를 처리하기 위한 sercice 필요 (MemberServiceImpl)
	@Autowired private MemberServiceImpl member;
	@Autowired private CommonService common;
	
	@RequestMapping("/reply_insert.no")
	public String reply_insert(NoticeVO vo, MultipartFile file, HttpSession session) {
		if(! file.isEmpty()) {
			vo.setFilename(file.getOriginalFilename());
			vo.setFilepath( common.fileUpload("notice", file, session));
		}
		vo.setWriter(((MemberVO) session.getAttribute("loginInfo")).getId()); 
		service.notice_reply_insert(vo);
		return "redirect:/list.no";
	}
	
	@RequestMapping("/reply.no")
	public String reply(int id, Model model) {
		model.addAttribute("vo", service.notice_detail(id));
		return "notice/reply";
	}
	
	@RequestMapping("/update.no")
	public String update(NoticeVO vo, String attach
					, MultipartFile file, HttpSession session) {
		NoticeVO notice = service.notice_detail(vo.getId());
		String uuid = session.getServletContext().getRealPath("resources") 
				+ "/" + notice.getFilepath();
		
		if(! file.isEmpty()) {
			vo.setFilename(file.getOriginalFilename());
			vo.setFilepath(common.fileUpload("notice", file, session));
			if ( notice.getFilename() != null ) {	// 서버에 파일이 있는지 파악
				File f = new File(uuid);
				if (f.exists())  f.delete();
			}else {
				if ( attach.isEmpty() ) {
					if (notice.getFilename() != null) {
						File f = new File(uuid);
						if (f.exists())  f.delete();
					}
				}else {
					vo.setFilename(uuid);
				}
			}
		}
		
		service.notice_update(vo);
		
		return "redirect:detail.no?id=" + vo.getId();
	}
	
	@RequestMapping ("/modify.no")
	public String modify(int id, Model model) {
		model.addAttribute("vo", service.notice_detail(id));
		return "notice/modify";
	}
	
	@RequestMapping("/delete.no")
	public String delete(int id, HttpSession session) {
		
		NoticeVO notice = service.notice_detail(id);
		String uuid = session.getServletContext().getRealPath("resources") 
					+ "/" + notice.getFilepath();
		if(notice.getFilename() != null) {
			File file = new File(uuid);
			if(file.exists()) file.delete();
		}
		
		service.notice_delete(id);
		return "redirect:list.no";
	}
	
	@RequestMapping ("/download.no")
	public void download(int id,HttpSession session, HttpServletResponse response) {
		NoticeVO notice = service.notice_detail(id);
		common.fileDownload(notice.getFilename(),notice.getFilepath(),session, response);
	}
	
	
	@RequestMapping("detail.no")
	public String detail(int id, Model model) {
		// 상세화면 요청 전
		service.notice_read(id);
		// 선택한 공지사항 정보를 DB에서 조회해와 상세화면에 출력
		model.addAttribute("vo",service.notice_detail(id));
		model.addAttribute("crlf","\r\n");
		
		model.addAttribute("page",page);
		return "notice/detail";
	}
	
	
	@RequestMapping("/insert.no")
	public String insert(NoticeVO vo, MultipartFile file, HttpSession session) {
		
//		MemberVO member = (MemberVO) session.getAttribute("loginInfo");
//		vo.setWriter(member.getId());
		vo.setWriter( ((MemberVO) session.getAttribute("loginInfo")).getId() );

		if(!file.isEmpty()) {
			vo.setFilename(file.getOriginalFilename());
			vo.setFilepath(common.fileUpload("notice",file,session));
		}
		
		service.notice_insert(vo);
		return "redirect:list.no";
	}
	
	
	// 신규 공지사항 입력(글쓰기) 화면 요청
	
	@RequestMapping ("/new.no")
	public String notice() {
		return "notice/new";
	}
	
	
	// 공지사항의 페이지 처리를 위해 개체로 선언한 NOticePage 클래스를
	// 자동 주입하여 사용하게 함.
	@Autowired private NoticePage page;
	
	@RequestMapping("/list.no")
	public String notice_list(HttpSession session, @RequestParam (defaultValue = "1") 
								int curPage, Model model, String search, String keyword) {
		
		//공지글 처리 중 임의로 로그인해 두기 (admin) -나중에 삭제할 것
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", "gimpoco");
		map.put("pw", "9611Sd22");
		
		session.setAttribute("loginInfo", member.member_login(map));
		
		session.setAttribute("category", "no");
		//DB에서 공지글 목록을 조회해와 목록화면에 출력
		//model.addAttribute("list",service.notice_list());
		
		page.setCurPage(curPage);
		
		page.setSearch(search);
		page.setKeyword(keyword);
		
		model.addAttribute("page", service.notice_list(page));
		
		
		return "notice/list";
	}
}
