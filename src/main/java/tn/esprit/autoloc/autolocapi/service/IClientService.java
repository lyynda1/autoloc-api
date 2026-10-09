package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.domain.Client;
import java.util.List;

public interface IClientService {
    List<Client> getAllClients();
    Client getClientById(Long id);
    Client saveClient(Client client);
    void deleteClient(Long id);
}