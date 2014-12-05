#!/bin/bash
tmux new-window -n 'Plumber' 'cd ~/Development/rads/platform/plumber && tail -f manager/logs/plumber.log'
tmux split-window -h 'cd ~/Development/rads/platform/plumber && tail -f worker/logs/plumber.log'
# tmux select-window -t hawkhost:0
# tmux -2 attach-session -t hawkhost