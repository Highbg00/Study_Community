package com.hanul.clcd;

import java.io.File;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import board.BoardCommentVO;
import board.BoardPage;
import board.BoardServiceImpl;
import board.BoardVO;
import common.CommonService;
import member.MemberVO;

@Controller
public class BoardController {
	
	@Autowired private BoardServiceImpl service;
	@Autowired private BoardPage page;
	@Autowired private CommonService common;
	
	@RequestMapping("/board/comment/list/{pid}")
	public String comment_list(@PathVariable int pid, Model model) {
		model.addAttribute("list", service.board_comment_list(pid));
		model.addAttribute("crlf","\r\n");
		model.addAttribute("lf","\n");
		
		
		return "board/comment/comment_list";
	}
	
	@ResponseBody
	@RequestMapping("/board/comment/regist")
	public boolean comment_regist(BoardCommentVO vo, HttpSession session) {
	
			MemberVO member = (MemberVO) session.getAttribute("loginInfo");
			vo.setWriter(member.getId());
			
			return service.board_comment_insert(vo) == 1 ? true : false;
		
	}
	
	
	@RequestMapping("/update.bo")
	public String update(BoardVO vo, HttpSession session, MultipartFile file, String attach, Model model) {
		BoardVO board = service.board_detail(vo.getId());
		String uuid = session.getServletContext().getRealPath("resources") + "/" + board.getFilepath();
		
		if(file.isEmpty()) {
			if(attach.isEmpty()) {
				if(board.getFilename()!=null) {
					File f = new File(uuid);
					if(f.exists()) f.delete();
				}
			}else {
				vo.setFilename(board.getFilename());
				vo.setFilepath(board.getFilepath());
			}
		}else {
			vo.setFilename(file.getOriginalFilename());
			vo.setFilepath(common.fileUpload("board", file, session));
			
			if(board.getFilename() != null) {
				File f = new File(uuid);
				if(f.exists()) f.delete();
			}
		}
		
		service.board_update(vo);
		
		model.addAttribute("url","detail.bo");
		model.addAttribute("id", vo.getId());
		
		return "board/redirect";
		//return "redirect:detail.bo?id=" + vo.getId();
	}
	
	
	@RequestMapping ("/modify.bo")
	public String modify(int id, Model model) {
		
		model.addAttribute("vo", service.board_detail(id));
		return "board/modify";
	}
	
	@RequestMapping("/delete.bo")
	public String delete(int id, HttpSession session, Model model) {
		BoardVO vo = service.board_detail(id);
		if( vo.getFilename() != null) {
			File file = new File(session.getServletContext().getRealPath("resources") + "/" + vo.getFilename());
			
			if(file.exists()) file.delete();
		}
		service.board_delete(id);
		
		model.addAttribute("url", "list.bo");
		
		model.addAttribute("page", page);	
		
		
		return "board/redirect";
	}
	
	
	@RequestMapping ("/download.bo")
	public void download(int id, HttpSession session, HttpServletResponse response) {
		BoardVO vo = service.board_detail(id);
		common.fileDownload(vo.getFilename(), vo.getFilepath(), session, response);
	}
	
	@RequestMapping ("/detail.bo")
	public String detail(int id, Model model) {
		
		service.board_read(id);
		
		// 해당 방명록 글을 DB에서 조회해와 상세화면에 출력
		model.addAttribute("vo", service.board_detail(id));
		model.addAttribute("crlf","\r\n");
		model.addAttribute("page", page);
		
		return "board/detail";
	}
	
	
	@RequestMapping ("/insert.bo")
	public String insert(BoardVO vo, MultipartFile file, HttpSession session) {
		
		if ( ! file.isEmpty() ) { // 파일 정보가 있다면
			vo.setFilename( file.getOriginalFilename());
			vo.setFilepath( common.fileUpload("board", file, session));
		}
		
		vo.setWriter( ((MemberVO) session.getAttribute("loginInfo")).getId() );
		
//		MemberVO member = (MemberVO) session.getAttribute("loginInfo");
//		vo.setWriter( member.getId() );
		
		// 화면에서 입력한 정보를 DB에 신규저장한 후 목록화면 연결
		service.board_insert(vo);
		return "redirect:list.bo";
	}
	
	@RequestMapping ("/new.bo")
	public String board() {
		return "board/new";
	}
	
	@RequestMapping("/list.bo")
	public String list(HttpSession session,Model model
						,@RequestParam(defaultValue = "1") int curPage
						,@RequestParam(defaultValue = "10") int pageList
						,@RequestParam(defaultValue = "list") String viewType
						,String search, String keyword) {
		session.setAttribute("category", "bo");
		
		page.setCurPage(curPage);
		page.setSearch(search);
		page.setKeyword(keyword);
		page.setPageList(pageList);
		page.setViewType(viewType);
		
		model.addAttribute("page",service.board_list(page));
		return "board/list";
	}
}
