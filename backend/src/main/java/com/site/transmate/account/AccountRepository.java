package com.site.transmate.account;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
	Optional<Account> findByAccountid(String accountid);
	Account findByName(String name);
	List<Account> findByNameLike(String name);

}
