package com.intuit.tms.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.tms.dto.TeamDTO;
import com.intuit.tms.entities.Team;
import com.intuit.tms.services.ErrorBuilderService;
import com.intuit.tms.services.TeamService;

import io.swagger.annotations.Api;

@RestController
@Api(description = "Team Management APIs", produces = "application/json", tags = { "A1" })
public class TeamManagementRestController {

	@Autowired
	private TeamService teamService;

	@Autowired
	private ErrorBuilderService errorBuilderService;

	@ModelAttribute("team")
	public TeamDTO teamDTO() {
		return new TeamDTO();
	}

	@PostMapping("/admin/api/v1/team")
	public ResponseEntity<Object> saveTeam(@ModelAttribute("team") @Valid TeamDTO teamDTO, BindingResult result) {

		Team existing = teamService.getTeamByName(teamDTO.getName());
		if (existing != null) {
			return new ResponseEntity<Object>("There is already exist a team with name as " + teamDTO.getName(),
					HttpStatus.BAD_REQUEST);
		}

		if (result.hasErrors()) {
			return new ResponseEntity<Object>(errorBuilderService.generateErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		try {
			teamService.saveTeam(teamDTO);
			return new ResponseEntity<Object>("You have successfully added a team", HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>("Team cannot be saved, Reason: " + ex.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/admin/api/v1/teams")
	public ResponseEntity<Object> getTeams() {

		try {
			List<Team> teams = teamService.getAllTeams();
			return new ResponseEntity<Object>(teams, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>("Teams cannot be fetched, Reason: " + ex.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/admin/api/v1/team/{teamId}")
	public ResponseEntity<Object> updateTeam(@PathVariable(name = "teamId") Long teamId,
			@ModelAttribute("team") @Valid TeamDTO teamDTO, BindingResult result) {

		Team existing = teamService.getTeamById(teamId);
		if (existing == null) {
			return new ResponseEntity<Object>("The team with team id as " + teamId + " is not found",
					HttpStatus.BAD_REQUEST);
		}

		if (result.hasErrors()) {
			return new ResponseEntity<Object>(errorBuilderService.generateErrorMessage(result), HttpStatus.BAD_REQUEST);
		}

		try {
			teamService.updateTeam(teamDTO, teamId);
			return new ResponseEntity<Object>("You have successfully updated a team with team id " + teamId,
					HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>("Team cannot be updated, Reason: " + ex.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/admin/api/v1/team/{teamId}")
	public ResponseEntity<Object> deleteTeam(@PathVariable(name = "teamId") Long teamId) {

		Team existing = teamService.getTeamById(teamId);
		if (existing == null) {
			return new ResponseEntity<Object>("The team with team id as " + teamId + " is not found",
					HttpStatus.BAD_REQUEST);
		}

		try {
			teamService.deleteTeam(teamId);
			return new ResponseEntity<Object>("Team successfully deleted", HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<Object>("Team cannot be deleted, Reason: " + ex.getMessage(),
					HttpStatus.BAD_REQUEST);
		}
	}
}
