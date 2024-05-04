package com.enesakkal.communityapp.services;
import com.enesakkal.communityapp.models.community.Community;
import com.enesakkal.communityapp.models.user.User;
import com.enesakkal.communityapp.repositories.CommunityRepository;
import com.enesakkal.communityapp.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    private final CommunityRepository repository;

    private final UserRepository userRepository;
    public CommunityService(CommunityRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Community addCommunity(Community community) {
        try {
            repository.save(community);
            return community;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Community getCommunityById(String id) {

        return repository.findById(id).orElseThrow(() -> new RuntimeException("Community not found"));
    }

    public void deleteCommunityById(String id) {
        repository.deleteById(id);
    }

    public boolean existsByDescription(String description) {
        return repository.existsByDescription(description);
    }

    public List<Community> getCommunities(String filter){
        if (filter == null) {
            return repository.findAll();
        }
        return repository.findAllByNameContaining(filter);
    }
    public List<Community> getCommunities(){
        return repository.findAll();
    }

    public Community createCommunity(Community community) {
        return repository.save(community);
    }

    public Community joinCommunity(String userId, String communityId) {

        Community community = repository.findById(communityId
        ).orElseThrow(() -> new RuntimeException("Community not found"));

        User user = userRepository.findBy_id(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(community.getMembers()==null){
            community.setMembers(List.of(user));
        }else{
           if(community.getMembers().contains(user)){
               throw new RuntimeException("User already in community");
           }
            community.getMembers().add(user);
        }
        community.setMemberCount(community.getMembers().size());

        if(user.getFollowedCommunities()==null){
            user.setFollowedCommunities(List.of(community.get_id()));
        }else{
            user.getFollowedCommunities().add(community.get_id());
        }

        userRepository.save(user);

        repository.save(community);

        return community;
    }
}