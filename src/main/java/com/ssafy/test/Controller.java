package com.ssafy.test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("api")
public class Controller {

	private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;

	@GetMapping(value = "users")
	public List<User> getUsers() {
		return userRepo.findAll();
	}
	
	@GetMapping(value = "user")
	public SingleResult<User> findUserById() {
		// SecurityContext에서 인증받은 회원의 정보를 얻어온다.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();
		// 결과데이터가 단일건인경우 getSingleResult를 이용해서 결과를 출력한다.
		return responseService.getSingleResult(userRepo.findByEmail(id).orElse(User.userNull()));
	}
	
	//@ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다.")
    @PostMapping(value = "signin")
    public SingleResult<String> signin( @RequestBody SignUpRequest rUser) {
		String id = rUser.getEmail();
		String password = rUser.getPassword();
        User user = userRepo.findByEmail(id).orElse(User.userNull());
        if (!passwordEncoder.matches(password, user.getPassword()))
            user = User.userNull();
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getEmail()), user.getRoles()));
	 }
	
	 //@ApiOperation(value = "가입", notes = "회원가입을 한다.")
	 @PostMapping(value = "signup")
	 public CommonResult signup( @RequestBody SignUpRequest rUser) {
		String id = rUser.getEmail();
		String password = rUser.getPassword();
		System.out.println(id +", "+ password);
        userRepo.save(User.builder()
                .email(id)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        return responseService.getSuccessResult();
    }
}
