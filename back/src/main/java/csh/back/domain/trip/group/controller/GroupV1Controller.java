package csh.back.domain.trip.group.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class GroupV1Controller {

	@GetMapping()
	public void getAllGroups() {

	}

	@PostMapping()
	public void saveGroup() {

	}

//	@GetMapping("/{groupId}")
//	public void getGroupDetail() {
//
//	}
}
