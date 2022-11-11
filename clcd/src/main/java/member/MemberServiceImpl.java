package member;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired private MemberDAO dao;
	
	@Override
	public boolean member_join(MemberVO vo) {
		// TODO Auto-generated method stub
		return dao.member_join(vo);
	}

	@Override
	public boolean member_update(MemberVO vo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean member_delete(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean member_id_check(String id) {
		// TODO Auto-generated method stub
		return dao.member_id_check(id);
	}

	@Override
	public MemberVO member_login(HashMap<String, String> map) {
		return dao.member_login(map);
	}
	
	@Override
	public boolean member_social_email(MemberVO vo) {
		// 소셜 회원 정보 존재여부 (R)
		return dao.member_social_email(vo);
	}

	@Override
	public boolean member_social_insert(MemberVO vo) {
		// 소셜회원정보 신규저장 (C)
		return dao.member_social_insert(vo);
	}

	@Override
	public boolean member_social_update(MemberVO vo) {
		// 소셜회원정보 변경저장 (U)
		return dao.member_social_update(vo);
	}
}
