(TeX-add-style-hook
 "main"
 (lambda ()
   (setq TeX-command-extra-options
         "-shell-escape")
   (TeX-add-to-alist 'LaTeX-provided-class-options
                     '(("oasics-v2019" "hyphens" "a4paper" "USenglish" "cleveref" "autoref" "thm-restate")))
   (add-to-list 'LaTeX-verbatim-environments-local "lstlisting")
   (add-to-list 'LaTeX-verbatim-macros-with-braces-local "lstinline")
   (add-to-list 'LaTeX-verbatim-macros-with-delims-local "lstinline")
   (TeX-run-style-hooks
    "latex2e"
    "oasics-v2019"
    "oasics-v201910"
    "listings"
    "color")
   (LaTeX-add-labels
    "fig:factorial"
    "fig:failed"
    "fig:checktrans"
    "fig:bugfix1"
    "fig:numbertype"
    "fig:currencyunits"
    "fig:spec"
    "fig:result")
   (LaTeX-add-bibliographies
    "bibliography")
   (LaTeX-add-listings-lstdefinestyles
    "scala"
    "stainless")
   (LaTeX-add-color-definecolors
    "dkgreen"
    "gray"
    "mauve"))
 :latex)

