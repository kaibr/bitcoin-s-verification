(TeX-add-style-hook
 "oasics-v2019-sample-article"
 (lambda ()
   (setq TeX-command-extra-options
         "-shell-escape")
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("oasics-v2019" "a4paper" "USenglish" "cleveref" "autoref" "thm-restate")))
   (TeX-run-style-hooks
    "latex2e"
    "oasics-v2019"
    "oasics-v201910")
   (LaTeX-add-labels
    "sec:typesetting-summary"
    "lemma:lorem"
    "lemma:curabitur"
    "prop1")
   (LaTeX-add-bibliographies
    "bibliography"))
 :latex)

