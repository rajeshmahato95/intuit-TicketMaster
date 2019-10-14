package com.intuit.tms.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.intuit.tms.entities.Account;
import com.intuit.tms.entities.Project;
import com.intuit.tms.entities.Status;
import com.intuit.tms.entities.Ticket;
import com.intuit.tms.entities.TicketType;
import com.intuit.tms.enums.TicketPriorityEnum;
import com.intuit.tms.enums.TicketResolutionEnum;
import com.intuit.tms.repositories.TicketRepository;
import com.intuit.tms.security.CustomUserDetailsService;
import com.intuit.tms.services.TicketService;
import com.intuit.tms.dto.TicketDTO;

@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@SuppressWarnings("serial")
	@Override
	public List<Ticket> serachTickets(Ticket filter) {
		@SuppressWarnings("unchecked")
		List<Ticket> tickets = ticketRepository.findAll(new Specification<Ticket>() {

			@Override
			public Predicate toPredicate(Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();

				// If assignee is specified in filter, add equal where clause
				if (filter.getAssignee() != null) {
					predicates.add(cb.equal(root.get("assignee"), filter.getAssignee()));
				}

				// If id is specified in filter, add equal where clause
				if (filter.getId() != null) {
					predicates.add(cb.equal(root.get("id"), filter.getId()));
				}

				// If priority is specified in filter, add equal where clause
				if (filter.getPriority() != null) {
					predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
				}

				// If priority is specified in filter, add equal where clause
				if (filter.getTicketType() != null) {
					predicates.add(cb.equal(root.get("ticketType"), filter.getTicketType()));
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			}

		});
		return tickets;
	}

	@Override
	public Ticket getTicketById(Long ticketId) {
		Optional<Ticket> ticket = ticketRepository.findById(ticketId);
		if (ticket.isPresent()) {
			return ticket.get();
		}
		return null;
	}

	@Override
	public Ticket saveTicket(TicketDTO ticketDTO) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long createdBy = customUserDetailsService.getAccountIdByUserName(auth.getName());
		Ticket ticket = new Ticket();

		// TODO: Here we will also write validation whether user is a valid user and
		// also have
		// privileges to be assigned to this project
		if (ticketDTO.getAssignee() != null) {
			ticket.setAssignee(new Account(ticketDTO.getAssignee()));
		} else {
			// Mocking the user 2 (user@intuit.com) to be assigned
			// TODO: Here we need to write the login to get the available user to be
			// assigned for this ticket among the users/teams associated to this project
			ticket.setAssignee(new Account(2L));
		}

		// TODO: We need to write a logic to convert the input date in LocalDateTime
		// format, for now mocking as current Date
		ticket.setDueDate(LocalDateTime.now());

		// TODO: We need to implement a logic to calculate the priority based on
		// severity and impact
		ticket.setPriority(TicketPriorityEnum.getByValue(ticketDTO.getPriority()));
		ticket.setResolution(TicketResolutionEnum.getByValue(ticketDTO.getResolution()));
		ticket.setStatus(new Status(ticketDTO.getStatus()));

		// TODO: Here we need to write the logic for validating a project also whether a
		// project support the current ticket type, etc
		// Mocking project
		ticket.setProject(new Project(ticketDTO.getProjectId()));

		ticket.setSummary(ticketDTO.getSummary());

		// TODO: Here we need to check whether a project support this ticket type also,
		// validate whether this ticket type exists
		// Mocking ticket type
		ticket.setTicketType(new TicketType(ticketDTO.getTicketType()));
		ticket.setDescription(ticketDTO.getDescription());
		ticket.setCreatedBy(createdBy);

		return ticketRepository.save(ticket);
	}

	@Override
	public List<Ticket> getAllTickets() {
		return ticketRepository.findAll();
	}

	@Override
	public Ticket updateTicket(TicketDTO ticketDTO, Long ticketId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long updatedBy = customUserDetailsService.getAccountIdByUserName(auth.getName());
		Ticket ticket = new Ticket();

		// TODO: Here we will also write validation whether user is a valid user and
		// also have
		// privileges to be assigned to this project
		if (ticketDTO.getAssignee() != null) {
			ticket.setAssignee(new Account(ticketDTO.getAssignee()));
		} else {
			// Mocking the user 2 (user@intuit.com) to be assigned
			// TODO: Here we need to write the login to get the available user to be
			// assigned for this ticket among the users/teams associated to this project
			ticket.setAssignee(new Account(2L));
		}

		// TODO: We need to write a logic to convert the input date in LocalDateTime
		// format, for now mocking as current Date
		ticket.setDueDate(LocalDateTime.now());

		// TODO: We need to implement a logic to calculate the priority based on
		// severity and impact
		ticket.setPriority(TicketPriorityEnum.getByValue(ticketDTO.getPriority()));
		ticket.setResolution(TicketResolutionEnum.getByValue(ticketDTO.getResolution()));
		ticket.setStatus(new Status(ticketDTO.getStatus()));

		// TODO: Here we need to write the logic for validating a project also whether a
		// project support the current ticket type, etc
		// Mocking project
		ticket.setProject(new Project(ticketDTO.getProjectId()));

		ticket.setSummary(ticketDTO.getSummary());

		// TODO: Here we need to check whether a project support this ticket type also,
		// validate whether this ticket type exists
		// Mocking ticket type
		ticket.setTicketType(new TicketType(ticketDTO.getTicketType()));
		ticket.setDescription(ticketDTO.getDescription());

		ticket.setUpdatedBy(updatedBy);
		ticket.setUpdatedOn(LocalDateTime.now());

		return ticketRepository.save(ticket);
	}

	@Override
	public void deleteTicket(Long ticketId) {
		ticketRepository.deleteById(ticketId);
	}
}