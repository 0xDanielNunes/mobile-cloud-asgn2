/*
 *
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.magnum.mobilecloud.video;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("JavaDoc")
@Controller
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    /**
     * You will need to create one or more Spring controllers to fulfill the
     * requirements of the assignment. If you use this file, please rename it
     * to something other than "AnEmptyController"
     * <p>
     * <p>
     * ________  ________  ________  ________          ___       ___  ___  ________  ___  __
     * |\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \
     * \ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_
     * \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \
     * \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \
     * \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
     * \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
     */

    /**
     * @param video
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public @ResponseBody
    Video addVideo(@RequestBody Video video) {

        return videoRepository.save(video);
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = "/video/{id}", method = RequestMethod.GET)
    public @ResponseBody
    Video getVideoById(@PathVariable Long id) {
        return videoRepository.findOne(id);
    }

    /**
     * @return
     */
    //No access to Guava or Apache so it's made with plain simple java
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public @ResponseBody
    Collection<Video> getAllVideos() {
        Collection<Video> list = new ArrayList<>();
        Iterable<Video> videoIterable = videoRepository.findAll();

        for (Video v : videoIterable)
            list.add(v);

        return list;
    }

    /**
     * @param id
     * @param p
     * @param httpResponse
     * @throws IOException
     */
    @RequestMapping(value = "video/{id}/like", method = RequestMethod.POST)
    public @ResponseBody
    void likeVideo(@PathVariable Long id, Principal p, HttpServletResponse httpResponse) throws IOException {
        //Transaction script
        if (!videoRepository.exists(id))
            httpResponse.sendError(404);

        Video v = videoRepository.findOne(id);

        //insures no duplicates
        Set<String> likedBy = v.getLikedBy();
        if (!likedBy.contains(p.getName())) {

            likedBy.add(p.getName());

            v.setLikes(v.getLikes() + 1);

            videoRepository.save(v);

            httpResponse.setStatus(200);
        } else {
            httpResponse.setStatus(400);

        }
    }

    /**
     * Same idea as in {@link #likeVideo(Long, Principal, HttpServletResponse)}
     *
     * @param id
     * @param p
     * @param httpResponse
     * @throws IOException
     */
    @RequestMapping(value = "video/{id}/unlike", method = RequestMethod.POST)
    public @ResponseBody
    void unlikeVideo(@PathVariable Long id, Principal p, HttpServletResponse httpResponse) throws IOException {

        if (!videoRepository.exists(id))
            httpResponse.sendError(404);

        Video v = videoRepository.findOne(id);

        Set<String> likedBy = v.getLikedBy();
        if (likedBy.contains(p.getName())) {

            likedBy.remove(p.getName());

            v.setLikes(v.getLikes() - 1);

            videoRepository.save(v);

            httpResponse.setStatus(200);
        } else {
            httpResponse.setStatus(400);
        }

    }

    /**
     * @param id
     * @param httpResponse
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/video/{id}/likedby", method = RequestMethod.GET)
    public @ResponseBody
    Set<String> getVideoLikedBy(@PathVariable Long id, HttpServletResponse httpResponse) throws IOException {

        if (!videoRepository.exists(id))
            httpResponse.sendError(404);

        Video v = videoRepository.findOne(id);

        httpResponse.setStatus(200);

        return v.getLikedBy();
    }

    @RequestMapping(value = "/video/search/findByDurationLessThan", method = RequestMethod.GET)
    public @ResponseBody
    Collection<Video> findVideoByDurationLessThan(@RequestParam Long duration) {
        return videoRepository.findByDurationLessThan(duration);
    }

    @RequestMapping(value = "/video/search/findByName", method = RequestMethod.GET)
    public @ResponseBody
    Collection<Video> findVideoByName(@RequestParam String title) {
        return videoRepository.findByName(title);
    }

}
