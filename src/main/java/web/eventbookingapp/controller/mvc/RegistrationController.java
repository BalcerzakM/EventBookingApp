package web.eventbookingapp.controller.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.eventbookingapp.dto.RegistrationDto;
import web.eventbookingapp.exception.EmailAlreadyExistsException;
import web.eventbookingapp.exception.UsernameAlreadyExistsException;
import web.eventbookingapp.service.UserService;

@Controller
@RequestMapping("/mvc/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String registration(
            Model model
    ) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "registration";
    }

    @PostMapping
    public String register(
            @Valid @ModelAttribute("registrationDto") RegistrationDto request,
            BindingResult bindingResult
    ){
        if (request.getPassword() != null && request.getConfirmPassword() != null) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Hasła nie są identyczne");
            }
        }

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try{
            userService.registerUser(request);
        }catch (UsernameAlreadyExistsException e) {
            bindingResult.rejectValue("username", "error.username", e.getMessage());
            return "registration";
        }catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "registration";
        }


        return "redirect:/mvc/login?registered";
    }

}